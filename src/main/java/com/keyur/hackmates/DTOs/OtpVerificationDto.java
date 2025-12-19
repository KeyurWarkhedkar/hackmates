package com.keyur.hackmates.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationDto {
    @NotBlank(message="Email is required!")
    String email;

    @NotBlank(message="Otp is required!")
    String otp;
}
