package com.keyur.hackmates.Controllers;

import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class ProfileController {
    //fields
    private final ProfileService profileService;

    //method to complete profile for user (bio, skills, profile photo and username)
    @PostMapping(value = "/complete-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> completeProfile(
            @RequestPart(value = "profilePhoto", required = false) MultipartFile photo,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "bio") String bio,
            @RequestParam(value = "interests") List<String> interests
            ) {
        System.out.println("Completion request received");
        return new ResponseEntity<>(profileService.completeProfile(photo, interests, bio, username), HttpStatus.OK);
    }
}
