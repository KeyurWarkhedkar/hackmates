package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.JwtResponseDto;
import com.keyur.hackmates.DTOs.LoginRequestDto;

public interface LoginLogoutService {
    public JwtResponseDto login(LoginRequestDto loginRequestDto);
}
