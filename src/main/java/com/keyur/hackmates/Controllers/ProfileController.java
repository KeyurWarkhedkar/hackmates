package com.keyur.hackmates.Controllers;

import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;
import com.keyur.hackmates.Services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/profile")
public class ProfileController {
    //fields
    private final ProfileService profileService;

    //method to complete profile for user. can be used for partial completion as well as
    //complete sign-up of normal users
}
