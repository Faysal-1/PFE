package com.maroctbib.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private long expiresIn;
    private String tokenType = "Bearer";
    private String refreshToken;
    private long refreshExpiresIn;
    private UserDto user;
}
