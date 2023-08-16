package com.codeleap.xilab.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailNotExistedException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public EmailNotExistedException(String email) {
		super(String.format("User with email [%s] does not exist", email));
	}

}
