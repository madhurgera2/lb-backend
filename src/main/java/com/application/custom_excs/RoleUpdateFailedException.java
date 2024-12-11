package com.application.custom_excs;

public class RoleUpdateFailedException extends RuntimeException {

	public RoleUpdateFailedException(String message) {
		super(message);
	}
}
