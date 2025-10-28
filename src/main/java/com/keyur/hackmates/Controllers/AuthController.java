package com.keyur.hackmates.Controllers;

import com.keyur.hackmates.CustomExceptions.ResourceNotFoundException;
import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.JwtResponseDto;
import com.keyur.hackmates.DTOs.LoginRequestDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;
import com.keyur.hackmates.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {
    //fields
    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;

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
    public ResponseEntity<ApiResponseDto> completeProfile(@Valid @ModelAttribute UserRegistrationDto userRegistrationDto) {
        return new ResponseEntity<>(authService.registerUser(userRegistrationDto), HttpStatus.OK);
    }

    //method for front end to get the jwt for o-auth users
    @GetMapping("/oauth/jwt")
    public ResponseEntity<JwtResponseDto> getOAuthJwt(@RequestParam String key) {
        String jwt = (String) redisTemplate.opsForValue().get(key);

        if (jwt == null) {
            throw new ResourceNotFoundException("Token not found or expired");
        }

        //delete after retrieval
        redisTemplate.delete(key);

        return ResponseEntity.ok(new JwtResponseDto(jwt, "Bearer", 3600));
    }

}
