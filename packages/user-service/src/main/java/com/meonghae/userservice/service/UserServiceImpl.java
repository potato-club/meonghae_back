package com.meonghae.userservice.service;

import com.meonghae.userservice.core.exception.impl.NotFoundException;
import com.meonghae.userservice.domin.FCMToken.FCMToken;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.dto.fcmtoken.FCMResponse;
import com.meonghae.userservice.service.port.FCMTokenRepository;
import com.meonghae.userservice.service.port.UserRepository;
import com.meonghae.userservice.service.client.feign.PetServiceClient;
import com.meonghae.userservice.service.client.feign.S3ServiceClient;
import com.meonghae.userservice.dto.file.S3Request;
import com.meonghae.userservice.dto.file.S3Response;
import com.meonghae.userservice.dto.file.S3Update;
import com.meonghae.userservice.dto.user.*;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.core.exception.impl.UnAuthorizedException;
import com.meonghae.userservice.core.jwt.JwtTokenProvider;
import com.meonghae.userservice.infra.repository.RedisService;
import lombok.RequiredArgsConstructor;
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

import static com.meonghae.userservice.core.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final S3ServiceClient s3Service;
    private final PetServiceClient petServiceClient;

    @Override
    @Transactional
    public UserResponse login(String email, HttpServletRequest request, HttpServletResponse response) {

        if (userRepository.existsByEmailAndDeleted(email, false)) {
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty()) {
                throw new NotFoundException("Not Found User", NOT_FOUND_EXCEPTION);
            }

            this.createToken(user.get().getUserRole(), email, request, response);

            return UserResponse.builder()
                    .responseCode("200_OK")
                    .build();

        } else if (userRepository.existsByEmailAndDeleted(email, true)) {
            throw new UnAuthorizedException("Already Withdrawal", NOT_ALLOW_WITHDRAWAL_EXCEPTION);
        }

        return UserResponse.builder()
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
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getNickname).orElse(null);
    }

    @Override
    public FCMResponse sendFCMToken(String email) {
        FCMToken fcmToken = fcmTokenRepository.findByEmail(email);

        if (fcmToken == null) {
            throw new UnAuthorizedException("Not Found", ACCESS_DENIED_EXCEPTION);
        }

        return FCMResponse.builder()
                .email(email)
                .fcmToken(fcmToken.getToken())
                .build();
    }

    @Override
    public UserMyPage viewMyPage(HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        });

        S3Response response = s3Service.viewUserFile(user.getEmail());

        if (response == null) {  // 등록한 사진이 없을 때
            return UserMyPage.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .birth(user.getBirth())
                    .build();
        } else {                    // 등록한 사진이 있을 때
            return UserMyPage.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .age(user.getAge())
                    .birth(user.getBirth())
                    .fileName(response.getFileName())
                    .fileUrl(response.getFileUrl())
                    .build();
        }
    }

    @Override
    @Transactional
    public void signUp(UserRequest userDto, HttpServletRequest request, HttpServletResponse response) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }

        userRepository.save(userDto.toDomain());

        if (userDto.getFile() != null) {
            S3Request s3Dto = new S3Request(userDto.getEmail(), "USER");
            s3Service.uploadFileForUser(userDto.getFile(), s3Dto);
        }

        this.createToken(UserRole.USER, userDto.getEmail(), request, response);
    }

    @Override
    @Transactional
    public void update(UserUpdate userDto, HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birth = LocalDate.parse(userDto.getBirth(), formatter);

        if (userDto.getFile() != null) {    // 업데이트 이미지가 있을 경우
            List<MultipartFile> fileList = new ArrayList<>();
            List<S3Update> updateList = new ArrayList<>();

            S3Response s3Response = s3Service.viewUserFile(email);

            if (s3Response == null) { // 원래 프로필 사진 없을 경우
                S3Request s3Dto = new S3Request(user.getEmail(), "USER");
                s3Service.uploadFileForUser(userDto.getFile(), s3Dto);
            } else { // 원래 프로필 사진 있을 경우
                S3Update s3Update = S3Update.builder()
                        .fileName(s3Response.getFileName())
                        .fileUrl(s3Response.getFileUrl())
                        .entityType(s3Response.getEntityType())
                        .email(s3Response.getEmail())
                        .deleted(true)
                        .build();

                fileList.add(userDto.getFile());
                updateList.add(s3Update);

                s3Service.updateImage(fileList, updateList);
            }
        }

        user.update(userDto, birth);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);

        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request), email);
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));

        fcmTokenRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void withdrawalMembership(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        user.delete(true);
        userRepository.save(user);

        this.logout(request);
    }

    @Override
    @Transactional
    public void cancelWithdrawal(String email, boolean agreement) {
        if (userRepository.existsByEmailAndDeleted(email, true) && agreement) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
            });

            user.delete(false);
            userRepository.save(user);

        } else {
            throw new UnAuthorizedException("401_NOT_ALLOW", NOT_ALLOW_WRITE_EXCEPTION);
        }
    }

    @Override
    @Transactional
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
        Map<String, String> refreshTokenData = redisService.getValues(email);

        if (refreshTokenData != null) { // 중복 로그인 시 먼저 로그인 한 기기의 토큰 정보 삭제 (로그아웃)
            String existingRefreshToken = refreshTokenData.get("refreshToken");
            redisService.delValues(existingRefreshToken, email);

            jwtTokenProvider.expireToken(refreshTokenData.get("accessToken"));
            petServiceClient.getReviseFcmToken(email, fcm); // Pet 서비스로 새 FCM 토큰 정보 전달
        }

        FCMToken fcmToken = fcmTokenRepository.findByEmail(email);

        if (fcmToken != null) {
            fcmToken.update(fcm);                   // 수정
        } else {
            fcmToken = FCMToken.builder()           // FCM 토큰 저장 준비
                    .token(fcm)
                    .email(email)
                    .build();
        }

        fcmTokenRepository.save(fcmToken);      // 저장

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
