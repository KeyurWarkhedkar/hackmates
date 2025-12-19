package com.keyur.hackmates.Services;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.UserSignupDto;
import com.keyur.hackmates.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    public User completeProfile(MultipartFile photo, List<String> interests, String bio, String username);
}
