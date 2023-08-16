package com.codeleap.xilab.api.payload.request.auth;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "The first name must not be blank")
    @Size(min = 2, max = 45,
            message = "The first name '${validatedValue}' must be between {min} and {max} characters long")
    private String firstName;

    @NotBlank(message = "The last name must not be blank")
    @Size(min = 2, max = 45,
            message = "The last name '${validatedValue}' must be between {min} and {max} characters long")
    private String lastName;

    @NotBlank(message = "Email should not be blank")
    @Size(min = 3, max = 80, message = "The email '${validatedValue}' must be between {min} and {max} characters long")
    @Email(message = "Email is not a valid pattern")
    private String email;

    private Set<String> role;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 6, max = 40,
            message = "The password '${validatedValue}' must be between {min} and {max} characters long")
    private String password;


    private String gender;

	private String industry;

	private String currentJob;

	private Short yearsOfExperience;

	private String linkedinLink;

	private String aboutMe;
}
