package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.JwtResponseDto;
import com.keyur.hackmates.DTOs.LoginRequestDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;

public interface AuthService {
    public JwtResponseDto login(LoginRequestDto loginRequestDto);
    public ApiResponseDto registerUser(UserRegistrationDto userRegistrationDto);
}
