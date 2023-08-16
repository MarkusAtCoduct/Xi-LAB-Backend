package com.codeleap.xilab.api.payload.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {

	private String token;

	private String type = "Bearer";

	private String refreshToken;

	private Long id;

	private String username;

	private String firstName;

	private String lastName;

	private List<String> roles;

}
