package com.meonghae.userservice.controller;

import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/users/{email}")
    public String getUserRoles(@PathVariable String email) {
        return userRepository.findByEmail(email).get().getUserRole().toString();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserRequestDto userDto) {
        userService.signUp(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @GetMapping("/login")
    public UserResponseDto login(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(code, request, response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @PutMapping("/")
    public ResponseEntity<String> updateNickname(@RequestBody UserRequestDto userDto, HttpServletRequest request) {
        userService.update(userDto, request);
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }
}
