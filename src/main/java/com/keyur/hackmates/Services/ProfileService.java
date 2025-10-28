package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;

public interface ProfileService {
    public ApiResponseDto completeProfile(UserRegistrationDto profileCompleteDto);
}
