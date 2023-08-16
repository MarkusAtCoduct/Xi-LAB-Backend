package com.codeleap.xilab.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidResetTokenException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public InvalidResetTokenException(String message) {
		super(message);
	}

}
