package com.codeleap.xilab.api.payload.request.auth;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TokenRefreshRequest {

	@NotBlank
	private String refreshToken;

}
