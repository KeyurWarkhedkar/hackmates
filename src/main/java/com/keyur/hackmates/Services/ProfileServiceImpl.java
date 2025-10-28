package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;
import com.keyur.hackmates.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    //fields
    UserRepository userRepository;

    //method to complete profile for user
    @Override
    @Transactional
    public ApiResponseDto completeProfile(UserRegistrationDto profileCompleteDto) {
        return null;
    }
}
