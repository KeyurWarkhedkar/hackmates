package com.keyur.hackmates.Controllers;

import com.keyur.hackmates.CustomExceptions.ResourceNotFoundException;
import com.keyur.hackmates.DTOs.*;
import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Repositories.UserRepository;
import com.keyur.hackmates.Security.JWT.JwtService;
import com.keyur.hackmates.Services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {
    //fields
    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    //dummy method to test out o-auth flow on localhost.
    @GetMapping("/home")
    public String profile() {
        return "Welcome to HackMates";
    }

    //method to login user without o-auth
    @PostMapping(value = "/login")
    private ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return new ResponseEntity<>(authService.login(loginRequestDto), HttpStatus.OK);
    }

    //method to register new user
    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponseDto> completeProfile(@Valid @RequestBody UserSignupDto userSignupDto) {
        return new ResponseEntity<>(authService.registerUser(userSignupDto), HttpStatus.OK);
    }

    //method to redirect the user to the first o-auth login page
    @GetMapping("/google")
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }


    //method for front end to get the jwt for o-auth users
    @GetMapping("/getJwt")
    public ResponseEntity<JwtResponseDto> getOAuthJwt(@RequestParam String key) {
        String jwt = (String) redisTemplate.opsForValue().get(key);

        if (jwt == null) {
            throw new ResourceNotFoundException("Token not found or expired");
        }

        //delete after retrieval
        redisTemplate.delete(key);

        String username = jwtService.extractUsername(jwt);

        User loggedInUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with the given email id!"));

        VaibhavDto user = new VaibhavDto(loggedInUser.getId(), loggedInUser.getFirstName(), loggedInUser.getEmail(), loggedInUser.getProfilePhotoUrl());

        return ResponseEntity.ok(new JwtResponseDto(jwt, "Bearer", 3600, user));
    }

    //method to verify user entered otp
    @PostMapping(value = "/verify-otp")
    public ResponseEntity<JwtResponseDto> verifyOtp(@RequestBody OtpVerificationDto otpVerificationDto) {
        return new ResponseEntity<>(authService.verifyOtp(otpVerificationDto), HttpStatus.OK);
    }
}
