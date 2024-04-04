package com.meonghae.userservice.web;

import com.meonghae.userservice.dto.fcmtoken.FCMResponse;
import com.meonghae.userservice.dto.user.*;
import com.meonghae.userservice.infra.entity.UserEntity;
import com.meonghae.userservice.infra.repository.user.UserJpaRepository;
import com.meonghae.userservice.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(value = "USER_CONTROLLER", tags = "User Service 컨트롤러")
public class UserController {

    private final UserJpaRepository userJpaRepository;
    private final UserService userService;

    @Operation(summary = "Gateway 서비스 내 토큰 재발급 로직용 API")
    @GetMapping("/users")
    public String getUserRoles(@RequestParam String email) {
        Optional<UserEntity> user = userJpaRepository.findByEmail(email);
        return user.map(userEntity -> userEntity.getUserRole().toString()).orElse(null);
    }

    @Operation(summary = "Feign Client 전송용 API - 이메일")
    @GetMapping("/send/email")
    public String sendEmail(@RequestHeader("Authorization") String token) {
        return userService.sendEmail(token);
    }

    @Operation(summary = "Feign Client 전송용 API - 닉네임")
    @GetMapping("/send/{email}")
    public String sendNickname(@PathVariable String email) {
        return userService.sendNickname(email);
    }

    @Operation(summary = "Feign Client 전송용 API - FCMToken")
    @GetMapping("/send/token")
    public FCMResponse sendFCMToken(@RequestParam String email) {
        return userService.sendFCMToken(email);
    }

    @Operation(summary = "회원가입 API")
    @PostMapping(value = "/signup")
    public ResponseEntity<String> signUp(UserRequest userDto,
                                         HttpServletRequest request, HttpServletResponse response) {
        userService.signUp(userDto, request, response);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "내 정보 확인 API")
    @GetMapping("/mypage")
    public UserMyPage viewMyPage(HttpServletRequest request) {
        return userService.viewMyPage(request);
    }

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login")
    public UserResponse login(@RequestParam String email, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(email, request, response);
    }

    @Operation(summary = "로그아웃 API")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @Operation(summary = "회원정보 수정 API")
    @PutMapping("/mypage")
    public ResponseEntity<String> updateUser(UserUpdate userDto, HttpServletRequest request) {
        userService.update(userDto, request);
        return ResponseEntity.ok("내 정보가 변경되었습니다.");
    }

    @Operation(summary = "회원탈퇴 API")
    @PutMapping("/withdrawal")
    public ResponseEntity<String> withdrawalMembership(HttpServletRequest request) {
        userService.withDrawlMembership(request);
        return ResponseEntity.ok("회원탈퇴 처리 되었습니다");
    }

    @Operation(summary = "회원탈퇴 취소 API")
    @PutMapping("/cancel")
    public ResponseEntity<String> cancelWithdrawal(@RequestBody UserCancel cancelDto) {
        userService.cancelWithdrawal(cancelDto.getEmail(), cancelDto.isAgreement());
        return ResponseEntity.ok("회원탈퇴 처리가 취소되었습니다");
    }

    @Operation(summary = "JWT 토큰 재발급 API")
    @GetMapping("/reissue")
    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        userService.reissueToken(request, response);
        return ResponseEntity.ok("토큰 재발급이 완료되었습니다");
    }
}
