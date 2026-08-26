package com.project.SpringSecurity.service;

import com.project.SpringSecurity.entity.User;

public interface OAuth2UserService {

    User processGoogleUser(
            String email,
            String firstName,
            String lastName
    );
}