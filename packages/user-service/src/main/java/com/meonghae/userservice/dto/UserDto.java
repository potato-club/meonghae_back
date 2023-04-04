package com.meonghae.userservice.dto;

import com.meonghae.userservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String email;
    private String nickname;
    private UserRole userRole;
    private String roles;
}
