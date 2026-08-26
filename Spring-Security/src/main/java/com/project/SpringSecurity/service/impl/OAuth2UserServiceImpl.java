package com.project.SpringSecurity.service.impl;

import com.project.SpringSecurity.entity.User;
import com.project.SpringSecurity.enums.AuthProvider;
import com.project.SpringSecurity.enums.Role;
import com.project.SpringSecurity.repository.UserRepository;
import com.project.SpringSecurity.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User processGoogleUser(
            String email,
            String firstName,
            String lastName
    ) {

        return userRepository.findByEmail(email)
                .map(existingUser -> {

                    if (existingUser.getProvider() == AuthProvider.LOCAL) {
                        throw new IllegalStateException(
                                "An account with this email already exists. " +
                                        "Please login using email and password."
                        );
                    }

                    return existingUser;
                })
                .orElseGet(() -> {

                    User user = new User();

                    user.setEmail(email);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);

                    user.setPassword(null);

                    user.setRole(Role.USER);
                    user.setProvider(AuthProvider.GOOGLE);

                    return userRepository.save(user);
                });
    }
}