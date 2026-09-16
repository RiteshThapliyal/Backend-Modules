package com.practice.Payment_Integration.service;

import com.practice.Payment_Integration.dto.request.UserRequest;
import com.practice.Payment_Integration.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser (UserRequest request);

    UserResponse getUserById (Long id);

    List<UserResponse> getAllUsers ();
}
