package com.project.SpringSecurity.security;

import com.project.SpringSecurity.entity.User;
import com.project.SpringSecurity.service.OAuth2UserService;
import com.project.SpringSecurity.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final OAuth2UserService oauth2UserService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");

        if (email == null || email.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Google account email could not be retrieved"
            );
            return;
        }

        User user = oauth2UserService.processGoogleUser(
                email,
                firstName,
                lastName
        );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password("")
                        .roles(user.getRole().name())
                        .build();

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write(
                """
                {
                    "success": true,
                    "message": "Google login successful",
                    "accessToken": "%s",
                    "refreshToken": "%s"
                }
                """.formatted(accessToken, refreshToken)
        );
    }
}