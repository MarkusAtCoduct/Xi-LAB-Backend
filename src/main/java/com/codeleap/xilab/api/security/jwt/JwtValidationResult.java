package com.codeleap.xilab.api.security.jwt;

public enum JwtValidationResult {

	Ok, InvalidEnvironment, InvalidSignature, InvalidToken, TokenIsExpired, TokenIsUnsupported, ClaimsStringEmpty,
	UnknownIssue

}
