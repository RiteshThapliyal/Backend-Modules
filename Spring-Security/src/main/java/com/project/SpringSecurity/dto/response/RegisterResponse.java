package com.project.SpringSecurity.dto.response;

import com.project.SpringSecurity.enums.Role;
import lombok.Data;

@Data
public class RegisterResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean enabled;

}
