package com.meonghae.userservice.controller;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Api(value = "USER_CONTROLLER", tags = "User Service 컨트롤러")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Operation(summary = "Gateway 서비스 내 토큰 재발급 로직용 API")
    @GetMapping("/users/{email}")
    public String getUserRoles(@PathVariable String email) {
        return userRepository.findByEmail(email).get().getUserRole().toString();
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserRequestDto userDto) {
        userService.signUp(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login")
    public UserResponseDto login(@RequestParam String code, HttpServletResponse response) {
        return userService.login(code, response);
    }

    @Operation(summary = "로그아웃 API")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @Operation(summary = "회원정보 수정 API")
    @PutMapping("/")
    public ResponseEntity<String> updateNickname(@RequestBody UserRequestDto userDto, HttpServletRequest request) {
        userService.update(userDto, request);
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }
}
