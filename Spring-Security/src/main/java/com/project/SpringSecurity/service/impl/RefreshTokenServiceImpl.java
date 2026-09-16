package com.project.SpringSecurity.service.impl;

import com.project.SpringSecurity.dto.response.LoginResponse;
import com.project.SpringSecurity.entity.RefreshToken;
import com.project.SpringSecurity.entity.User;
import com.project.SpringSecurity.exception.registration.InvalidRefreshTokenException;
import com.project.SpringSecurity.repository.RefreshTokenRepository;
import com.project.SpringSecurity.security.JwtService;
import com.project.SpringSecurity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    @Override
    public String createRefreshToken(User user) {

        try
        {
            SecureRandom secureRandom = new SecureRandom();

            byte[] selectorBytes = new byte[16];
            byte[] secretBytes = new byte[32];

            secureRandom.nextBytes(selectorBytes);
            secureRandom.nextBytes(secretBytes);

            String selector = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(selectorBytes);

            String secret = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(secretBytes);

            String rawRefreshToken = selector + "." + secret;

            String tokenHash = passwordEncoder.encode(secret);

            RefreshToken refreshToken = new RefreshToken();

            refreshToken.setSelector(selector);
            refreshToken.setTokenHash(tokenHash);
            refreshToken.setUser(user);
            refreshToken.setExpiresAt(
                    Instant.now().plusMillis(refreshTokenExpiration)
            );
            refreshToken.setRevoked(false);

            refreshTokenRepository.save(refreshToken);

            return rawRefreshToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse verifyRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is required"
            );
        }

        String[] parts = token.split("\\.", 2);

        if (parts.length != 2 ||
                parts[0].isBlank() ||
                parts[1].isBlank()) {

            throw new InvalidRefreshTokenException(
                    "Invalid refresh token format"
            );
        }

        String selector = parts[0];
        String secret = parts[1];

        RefreshToken refreshToken =
                refreshTokenRepository.findBySelector(selector)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        if (!passwordEncoder.matches(
                secret,
                refreshToken.getTokenHash()
        )) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        User user = refreshToken.getUser();

        if (user == null) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is not associated with a user"
            );
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        return LoginResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(token)
                .build();
    }

    @Transactional
    @Override
    public void revokeRefreshToken(String token) {

        if (token == null || token.isBlank()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is required"
            );
        }

        String[] parts = token.split("\\.", 2);

        if (parts.length != 2 ||
                parts[0].isBlank() ||
                parts[1].isBlank()) {

            throw new InvalidRefreshTokenException(
                    "Invalid refresh token format"
            );
        }

        String selector = parts[0];
        String secret = parts[1];

        RefreshToken refreshToken =
                refreshTokenRepository.findBySelector(selector)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        if (!passwordEncoder.matches(
                secret,
                refreshToken.getTokenHash()
        )) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        if (refreshToken.isRevoked()) {
            return;
        }

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}
