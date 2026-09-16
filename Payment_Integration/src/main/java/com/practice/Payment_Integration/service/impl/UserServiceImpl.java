package com.practice.Payment_Integration.service.impl;

import com.practice.Payment_Integration.dto.request.UserRequest;
import com.practice.Payment_Integration.dto.response.UserResponse;
import com.practice.Payment_Integration.entity.User;
import com.practice.Payment_Integration.exception.EmailAlreadyExistsException;
import com.practice.Payment_Integration.exception.UserNotFoundException;
import com.practice.Payment_Integration.repository.UserRepository;
import com.practice.Payment_Integration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser (UserRequest request)
    {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists: " + request.getEmail()
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse (savedUser);
    }

    @Override
    public UserResponse getUserById (Long id)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers ()
    {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
