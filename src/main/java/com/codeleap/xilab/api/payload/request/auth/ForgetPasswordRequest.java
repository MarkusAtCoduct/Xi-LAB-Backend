package com.codeleap.xilab.api.payload.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ForgetPasswordRequest {

	@NotBlank(message = "Email should not be blank")
	@Size(min = 3, max = 80, message = "The email '${validatedValue}' must be between {min} and {max} characters long")
	@Email(message = "Email is not a valid pattern")
	private String email;

	@NotBlank(message = "Reset password URL should not be blank")
	@Size(min = 10, message = "Invalid web link")
	private String resetPasswordUrl;

}
