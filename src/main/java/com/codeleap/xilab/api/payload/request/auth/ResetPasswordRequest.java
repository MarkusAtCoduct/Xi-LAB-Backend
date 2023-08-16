package com.codeleap.xilab.api.payload.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ResetPasswordRequest {

	@NotBlank(message = "New password should not be blank")
	@Size(min = 6, max = 40,
			message = "The password '${validatedValue}' must be between {min} and {max} characters long")
	private String newPassword;

	@NotBlank(message = "Reset token should not be blank")
	private String resetToken;

}
