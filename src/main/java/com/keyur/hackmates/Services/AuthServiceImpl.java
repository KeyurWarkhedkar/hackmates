package com.keyur.hackmates.Services;

import com.keyur.hackmates.CustomExceptions.DuplicateEmailException;
import com.keyur.hackmates.DTOs.ApiResponseDto;
import com.keyur.hackmates.DTOs.JwtResponseDto;
import com.keyur.hackmates.DTOs.LoginRequestDto;
import com.keyur.hackmates.DTOs.UserRegistrationDto;
import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Repositories.UserRepository;
import com.keyur.hackmates.Security.JWT.JwtService;
import com.keyur.hackmates.Security.Users.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    //fields
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final CloudinaryService cloudinaryService;

    private final String duplicateEmailMessage = "This email is already in use. Try again with another email!";

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", userDetails.getUsername());
            claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

            String jwtToken = jwtService.generateToken(claims, userDetails.getUsername());

            return new JwtResponseDto(jwtToken, "Bearer", 3600);
        } catch(AuthenticationException exception) {
            throw new BadCredentialsException("Invalid Credentials");
        }
    }

    //method to register user to db in normal auth
    @Override
    @Transactional
    public ApiResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        //check if a user with the given email already exists in the database
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDto.getEmail());
        if(existingUser.isPresent()) {
            throw new DuplicateEmailException(duplicateEmailMessage);
        }

        //create new user and save to database
        User newUser = new User();
        newUser.setEmail(userRegistrationDto.getEmail());
        newUser.setPassword(encoder.encode(userRegistrationDto.getPassword()));
        newUser.setFirstName(userRegistrationDto.getFirstName());
        newUser.setLastName(userRegistrationDto.getLastName());
        newUser.setBio(userRegistrationDto.getBio());
        newUser.setPhoneNumber(userRegistrationDto.getPhoneNumber());
        newUser.setRole("USER");
        newUser.setActive(true);
        newUser.setProfileComplete(false);

        //upload the photo to cloudinary and save the url to database
        if(userRegistrationDto.getProfilePhoto() != null && !userRegistrationDto.getProfilePhoto().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(userRegistrationDto.getProfilePhoto());
            newUser.setProfilePhotoUrl(imageUrl);
        }

        //use db-unique constraint as final safety net for concurrent registrations
        try {
            userRepository.save(newUser);
        } catch(DataIntegrityViolationException exception) {
            throw new DuplicateEmailException(duplicateEmailMessage);
        }

        return new ApiResponseDto("User registered successfully!");
    }
}
