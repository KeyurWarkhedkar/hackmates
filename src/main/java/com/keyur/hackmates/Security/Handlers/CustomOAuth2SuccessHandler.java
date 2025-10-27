package com.keyur.hackmates.Security.Handlers;

import com.keyur.hackmates.Entities.User;
import com.keyur.hackmates.Repositories.UserRepository;
import com.keyur.hackmates.Security.JWT.JwtService;
import com.keyur.hackmates.Security.Users.CustomUserDetailsService;
import com.keyur.hackmates.Services.TempCodeStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final TempCodeStore tempCodeStore;
    private final UserRepository userRepository;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(oAuth2User.getAttribute("given_name"));
            newUser.setLastName(oAuth2User.getAttribute("family_name"));
            newUser.setProfilePhotoUrl(oAuth2User.getAttribute("picture"));
            newUser.setRole("USER");
            return userRepository.save(newUser);
        });

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", user.getRole());

        String jwt = jwtService.generateToken(claims, user.getEmail());
        String code = UUID.randomUUID().toString();

        tempCodeStore.save(code, jwt);

        String redirectUrl = "http://localhost:8080/home?code=" + code;
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}

