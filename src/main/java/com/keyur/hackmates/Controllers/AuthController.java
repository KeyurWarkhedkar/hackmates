package com.keyur.hackmates.Controllers;

import com.keyur.hackmates.DTOs.JwtResponseDto;
import com.keyur.hackmates.DTOs.LoginRequestDto;
import com.keyur.hackmates.Services.LoginLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    //fields
    private final LoginLogoutService loginLogoutService;

    //dummy method to test out o-auth flow on localhost.
    @GetMapping("/home")
    public String profile() {
        //Return Google user info as JSON
        return "Welcome to HackMates";
    }

    //method to login user without o-auth
    @PostMapping(value = "/auth/login")
    private ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return new ResponseEntity<>(loginLogoutService.login(loginRequestDto), HttpStatus.OK);
    }
}
