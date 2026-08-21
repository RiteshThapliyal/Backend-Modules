package com.project.SpringSecurity.service.impl;

import com.project.SpringSecurity.common.constants.ApiMessages;
import com.project.SpringSecurity.dto.request.LoginRequest;
import com.project.SpringSecurity.dto.request.RegisterRequest;
import com.project.SpringSecurity.dto.response.LoginResponse;
import com.project.SpringSecurity.dto.response.RegisterResponse;
import com.project.SpringSecurity.entity.User;
import com.project.SpringSecurity.enums.Role;
import com.project.SpringSecurity.exception.registration.EmailAlreadyExistException;
import com.project.SpringSecurity.repository.UserRepository;
import com.project.SpringSecurity.security.JwtService;
import com.project.SpringSecurity.service.AuthService;
import com.project.SpringSecurity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
        {
            throw new EmailAlreadyExistException(ApiMessages.EMAIL_ALREADY_EXISTS);
        }

        User user = mapRequestToUser (request);

        User savedUser = userRepository.save(user);

        return mapUserToRegisterResponse (savedUser);
    }

    private User mapRequestToUser(RegisterRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return user;
    }

    private RegisterResponse mapUserToRegisterResponse(User savedUser) {
        RegisterResponse response = new RegisterResponse();

        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setEnabled(savedUser.isEnabled());

        return response;
    }

//    LOGIN SERVICE LOGIC

    @Override
    public LoginResponse login (LoginRequest request)
    {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        String accessToken = jwtService.generateAccessToken(userDetails);

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .email(userDetails.getUsername())
                .role(userDetails.getAuthorities()
                        .stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse(null))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
