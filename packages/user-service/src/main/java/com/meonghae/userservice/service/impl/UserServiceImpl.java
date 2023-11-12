package com.meonghae.userservice.service.impl;

import com.meonghae.userservice.client.PetServiceClient;
import com.meonghae.userservice.client.S3ServiceClient;
import com.meonghae.userservice.dto.*;
import com.meonghae.userservice.dto.S3Dto.S3RequestDto;
import com.meonghae.userservice.dto.S3Dto.S3ResponseDto;
import com.meonghae.userservice.dto.S3Dto.S3UpdateDto;
import com.meonghae.userservice.entity.FCMToken;
import com.meonghae.userservice.entity.User;
import com.meonghae.userservice.enums.UserRole;
import com.meonghae.userservice.error.ErrorCode;
import com.meonghae.userservice.error.exception.UnAuthorizedException;
import com.meonghae.userservice.jwt.JwtTokenProvider;
import com.meonghae.userservice.repository.FCMTokenRepository;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.Jwt.RedisService;
import com.meonghae.userservice.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.meonghae.userservice.error.ErrorCode.*;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final S3ServiceClient s3Service;
    private final PetServiceClient petServiceClient;

    @Override
    public UserResponseDto login(String email, HttpServletRequest request, HttpServletResponse response) {

        if (userRepository.existsByEmailAndDeleted(email, false)) {
            UserRole userRole = userRepository.findByEmail(email).get().getUserRole();
            this.createToken(userRole, email, request, response);

            return UserResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        } else if (userRepository.existsByEmailAndDeleted(email, true)) {
            throw new UnAuthorizedException("Already Withdrawal", NOT_ALLOW_WITHDRAWAL_EXCEPTION);
        }

        return UserResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")    // 회원가입 필요
                .build();
    }

    @Override
    public String sendEmail(String token) {
        return jwtTokenProvider.getUserEmail(token);
    }

    @Override
    public String sendNickname(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(User::getNickname).orElse(null);
    }

    @Override
    public FCMResponseDto sendFCMToken(String email) {
        FCMToken fcmToken = fcmTokenRepository.findByEmail(email);
        if (fcmToken == null) {
            throw new UnAuthorizedException("Not Found", ACCESS_DENIED_EXCEPTION);
        }

        return FCMResponseDto.builder()
                .email(email)
                .FCMToken(fcmToken.getToken())
                .build();
    }

    @Override
    public UserMyPageDto viewMyPage(HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        });

        S3ResponseDto responseDto = s3Service.viewUserFile(user.getEmail());

        if (responseDto == null) {  // 등록한 사진이 없을 때
            return UserMyPageDto.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .birth(user.getBirth())
                    .build();
        } else {                    // 등록한 사진이 있을 때
            return UserMyPageDto.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .birth(user.getBirth())
                    .fileName(responseDto.getFileName())
                    .fileUrl(responseDto.getFileUrl())
                    .build();
        }
    }

    @Override
    public void signUp(UserRequestDto userDto, HttpServletRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }
        log.info("127Line_request_fcm: "+request.getHeader("FCMToken").toString());

        userRepository.save(userDto.toEntity());
        MultipartFile file = userDto.getFile();

        if(file != null) {
            S3RequestDto s3Dto = new S3RequestDto(userDto.getEmail(), "USER");
            s3Service.uploadFileForUser(file, s3Dto);
        }

        UserRole userRole = userRepository.findByEmail(userDto.getEmail()).get().getUserRole();
        this.createToken(userRole, userDto.getEmail(), request, response);
    }

    @Override
    public void update(UserUpdateDto userDto, HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birth = LocalDate.parse(userDto.getBirth(), formatter);

        if (userDto.getFile() != null) {
            List<MultipartFile> fileList = new ArrayList<>();
            List<S3UpdateDto> updateList = new ArrayList<>();

            S3ResponseDto s3ResponseDto = s3Service.viewUserFile(email);

            S3UpdateDto s3UpdateDto = S3UpdateDto.builder()
                    .fileName(s3ResponseDto.getFileName())
                    .fileUrl(s3ResponseDto.getFileUrl())
                    .entityType(s3ResponseDto.getEntityType())
                    .email(s3ResponseDto.getEmail())
                    .deleted(true)
                    .build();

            fileList.add(userDto.getFile());
            updateList.add(s3UpdateDto);

            s3Service.updateImage(fileList, updateList);
        }

        user.update(userDto, birth);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request), email);
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));
        fcmTokenRepository.deleteByEmail(email);
    }

    @Override
    public void withdrawalMembership(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        user.setDeleted(true);
        this.logout(request);
    }

    @Override
    public void cancelWithdrawal(String email, boolean agreement) {
        if (userRepository.existsByEmailAndDeleted(email, true) && agreement) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
            });

            user.setDeleted(false);

        } else {
            throw new UnAuthorizedException("401_NOT_ALLOW", NOT_ALLOW_WRITE_EXCEPTION);
        }
    }

    @Override
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String androidId = request.getHeader("androidId");

        String email = jwtTokenProvider.getUserEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        });

        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getUserRole(), androidId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email, user.getUserRole(), androidId);

        // Redis에서 기존 리프레시 토큰과 Android-Id를 삭제한다.
        redisService.delValues(refreshToken, email);

        // Redis에 새로운 리프레시 토큰과 Android-Id를 저장한다.
        redisService.setValues(newRefreshToken, email, androidId);
        redisService.setValues(email, androidId, newAccessToken, newRefreshToken);

        // 헤더에 토큰들을 넣는다.
        jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, newRefreshToken);
    }

    private String findByEmailFromAccessToken(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }

    private void createToken(UserRole userRole, String email, HttpServletRequest request, HttpServletResponse response) {
        String androidId = request.getHeader("androidId");
        String fcm = request.getHeader("FCMToken");
        log.info(" 235line_fcm: "+fcm);
        Map<String, String> refreshTokenData = redisService.getValues(email);

        if (refreshTokenData != null) { // 중복 로그인 시 먼저 로그인 한 기기의 토큰 정보 삭제 (로그아웃)
            log.info(" 239line_IsDuplicate! ");
            String existingRefreshToken = refreshTokenData.get("refreshToken");
            redisService.delValues(existingRefreshToken, email);
            jwtTokenProvider.expireToken(refreshTokenData.get("accessToken"));
            fcmTokenRepository.deleteByEmail(email);    // 기존 토큰 정보 삭제
            petServiceClient.getReviseFcmToken(email, fcm); // Pet 서비스로 새 FCM 토큰 정보 전달
        }
        log.info(" 246line ");
        FCMToken fcmToken = FCMToken.builder()  // FCM 토큰 저장 준비
                .token(fcm)
                .email(email)
                .build();

        fcmTokenRepository.save(fcmToken);      // 저장
        log.info(" 253line ");
        // 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(email, userRole, androidId);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole, androidId);

        // 발급한 토큰을 헤더에 삽입
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        // redis에 토큰 정보 저장
        redisService.setValues(refreshToken, email, androidId);
        redisService.setValues(email, androidId, accessToken, refreshToken);
    }
}
