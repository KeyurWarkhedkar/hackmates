package com.keyur.hackmates.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDto {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
}
