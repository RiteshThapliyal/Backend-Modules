package com.project.SpringSecurity.service;

import com.project.SpringSecurity.dto.request.LoginRequest;
import com.project.SpringSecurity.dto.request.RegisterRequest;
import com.project.SpringSecurity.dto.response.LoginResponse;
import com.project.SpringSecurity.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register (RegisterRequest request);

    LoginResponse login (LoginRequest request);
}
