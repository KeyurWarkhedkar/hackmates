package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.*;

public interface AuthService {
    public JwtResponseDto login(LoginRequestDto loginRequestDto);
    public ApiResponseDto registerUser(UserSignupDto userRegistrationDto);
    public JwtResponseDto verifyOtp(OtpVerificationDto otpVerificationDto);
}
