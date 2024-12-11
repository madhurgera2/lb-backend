package com.application.custom_excs;

public class InvalidRoleException extends RuntimeException {
	
	public InvalidRoleException(String message) {
		super(message);
	}
}