package com.project.SpringSecurity.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RegisterRequest {
    @NotBlank(message = "First Name is Required")
    @Size(min = 2, max = 30, message = "First Name must be between 2 and 30 Characters")
    private String firstName;

    @NotBlank(message = "Last Name is Required")
    @Size(min = 2, max = 30, message = "Last Name must be between 2 to 30 Characters")
    private String lastName;

    @NotBlank(message = "Email is Required")
    @Email(message = "Enter a Valid Email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 Characters")
    private String password;
}
