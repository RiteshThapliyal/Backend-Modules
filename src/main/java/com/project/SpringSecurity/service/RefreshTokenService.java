package com.project.SpringSecurity.service;

import com.project.SpringSecurity.dto.response.LoginResponse;
import com.project.SpringSecurity.entity.RefreshToken;
import com.project.SpringSecurity.entity.User;

public interface RefreshTokenService {
    String createRefreshToken (User user);

    LoginResponse verifyRefreshToken (String token);
}
