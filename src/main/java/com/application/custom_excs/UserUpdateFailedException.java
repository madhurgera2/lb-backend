package com.application.custom_excs;

public class UserUpdateFailedException extends RuntimeException {

	public UserUpdateFailedException(String message) {
		super(message);
	}
}
