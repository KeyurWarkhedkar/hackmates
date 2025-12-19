package com.keyur.hackmates.Services;

import com.keyur.hackmates.CustomExceptions.DuplicateEmailException;
import com.keyur.hackmates.CustomExceptions.InvalidOperationException;
import com.keyur.hackmates.CustomExceptions.ResourceNotFoundException;
import com.keyur.hackmates.DTOs.*;
import com.keyur.hackmates.Entities.EmailOtp;
import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Repositories.EmailOtpRepository;
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

import java.time.LocalDateTime;
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
    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;

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

            return new JwtResponseDto(jwtToken, "Bearer", 3600, new VaibhavDto());
        } catch(AuthenticationException exception) {
            throw new InvalidOperationException("Some error occurred. Please try again!");
        }
    }

    //method to register user to db in normal auth
    @Override
    @Transactional
    public ApiResponseDto registerUser(UserSignupDto userRegistrationDto) {
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
        //newUser.setUsername(userRegistrationDto.getUsername());
        //newUser.setBio(userRegistrationDto.getBio());
        //newUser.setPhoneNumber(userRegistrationDto.getPhoneNumber());*/
        newUser.setRole("USER");
        newUser.setActive(true);
        newUser.setProfileComplete(false);

//        //upload the photo to cloudinary and save the url to database
//        if(userRegistrationDto.getProfilePhoto() != null && !userRegistrationDto.getProfilePhoto().isEmpty()) {
//            String imageUrl = cloudinaryService.uploadImage(userRegistrationDto.getProfilePhoto());
//            newUser.setProfilePhotoUrl(imageUrl);
//        }

        //use db-unique constraint as final safety net for concurrent registrations
        try {
            userRepository.save(newUser);
        } catch(DataIntegrityViolationException exception) {
            throw new DuplicateEmailException(duplicateEmailMessage);
        }

        //after saving the user, send the otp mail for confirmation
        String otp = com.keyur.hackmates.Utils.OtpUtil.generateOtp(6);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setUser(newUser);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        emailOtpRepository.save(emailOtp);

        //send OTP email
        emailService.sendOtpEmail(newUser.getEmail(), otp);

        return new ApiResponseDto("OTP sent to email");
    }

    //method to verify otp entered by the user
    @Transactional
    public JwtResponseDto verifyOtp(OtpVerificationDto otpVerificationDto) {
        final String email = otpVerificationDto.getEmail();
        final String otp = otpVerificationDto.getOtp();

        //check if a user with the given mail id is registered or not
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with the given email!"));

        //check if an unverified otp exists for this user or not
        EmailOtp emailOtp = emailOtpRepository.findByUser_IdAndIsVerified(user.getId(), false)
                .orElseThrow(() -> new ResourceNotFoundException("No unverified otp found for the given user!"));

        //check if the otp is not expired
        if(emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("Time for OTP verification expired. Please try again!");
        }

        //check if the entered otp matches with the saved otp
        String savedOtp = emailOtp.getOtp();
        if(!savedOtp.equals(otp)) {
            throw new InvalidOperationException("Wrong OTP. Please try again!");
        }

        //set the OTP as verified
        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        //mark the user as verified as well
        user.setVerified(true);

        //generate the jwt and log in the user
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        String jwtToken = jwtService.generateToken(claims, user.getEmail());

        return new JwtResponseDto(jwtToken, "Bearer", 3600, new VaibhavDto());
    }
}
