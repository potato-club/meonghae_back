package com.meonghae.userservice.service.impl;

import com.meonghae.userservice.client.S3ServiceClient;
import com.meonghae.userservice.dto.S3Dto.S3RequestDto;
import com.meonghae.userservice.dto.S3Dto.S3ResponseDto;
import com.meonghae.userservice.dto.S3Dto.S3UpdateDto;
import com.meonghae.userservice.dto.UserMyPageDto;
import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.dto.UserUpdateDto;
import com.meonghae.userservice.entity.User;
import com.meonghae.userservice.enums.UserRole;
import com.meonghae.userservice.error.exception.UnAuthorizedException;
import com.meonghae.userservice.jwt.JwtTokenProvider;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.Jwt.RedisService;
import com.meonghae.userservice.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.meonghae.userservice.error.ErrorCode.ACCESS_DENIED_EXCEPTION;
import static com.meonghae.userservice.error.ErrorCode.NOT_ALLOW_WRITE_EXCEPTION;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final S3ServiceClient s3Service;

    @Override
    public UserResponseDto login(String email, HttpServletRequest request, HttpServletResponse response) {

        if (userRepository.existsByEmailAndDeleted(email, false)) {
            UserRole userRole = userRepository.findByEmail(email).get().getUserRole();
            this.createToken(userRole, email, request, response);

            return UserResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        } else if (userRepository.existsByEmailAndDeleted(email, true)) {
            throw new UnAuthorizedException("401_NOT_ALLOW", NOT_ALLOW_WRITE_EXCEPTION);
        }

        return UserResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")
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
    public UserMyPageDto viewMyPage(HttpServletRequest request) {
        String email = this.findByEmailFromAccessToken(request);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        });

        S3ResponseDto responseDto = s3Service.viewUserFile(user.getEmail());
        return UserMyPageDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .age(user.getAge())
                .birth(user.getBirth().toLocalDate())
                .fileName(responseDto.getFileName())
                .fileUrl(responseDto.getFileUrl())
                .build();
    }

    @Override
    public void signUp(UserRequestDto userDto, HttpServletRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }
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
        LocalDateTime birth = LocalDateTime.parse(userDto.getBirth(), formatter);

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
        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request), this.findByEmailFromAccessToken(request));
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));
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

    private String findByEmailFromAccessToken(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }

    private void createToken(UserRole userRole, String email, HttpServletRequest request, HttpServletResponse response) {
        String androidId = request.getHeader("androidId");

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisService.setValues(refreshToken, email);
        redisService.setAndroidId(email, androidId);
    }
}
