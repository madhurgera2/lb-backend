package com.application.constants;

public enum ErrorCode {
	USER_NOT_FOUND("USER_NOT_FOUND", "User not found in the system", 404),
	INVALID_CREDENTIALS("INVALID_CREDENTIALS", "The provided credentials are incorrect", 401),
	INVALID_INPUT("INVALID_INPUT", "The input provided is invalid", 400),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred", 500),
	ACCESS_DENIED("ACCESS_DENIED", "You do not have permission to access this resource", 403),
	VALIDATION_ERROR("VALIDATION_ERROR", "There was an error during validation", 400),
	USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User with the provided email already exists", 400),
	USER_UPDATE_FAILED("USER_UPDATE_FAILED", "Failed to update the user profile", 500),
	ROLE_UPDATE_FAILED("ROLE_UPDATE_FAILED", "Failed to update the user role", 500),
	RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource could not be found", 404),
	NULL_POINTER("NULL_POINTER", "A null pointer error occurred", 500),
	CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "Validation constraint violated", 422),
	METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Request method not supported", 405),
	INVALID_TOKEN("INVALID_TOKEN", "The provided token is invalid", 401),
	JWT_EXPIRED("JWT_EXPIRED", "Your session has expired. Please log in again.", 401),
	EVENT_NOT_FOUND("EVENT_NOT_FOUND", "The requested event could not be found", 404),
	DONOR_NOT_FOUND("DONOR_NOT_FOUND", "The requested donor could not be found", 404);

	private final String code;
	private final String message;
	private final int httpStatusCode;

	ErrorCode(String code, String message, int httpStatusCode) {
		this.code = code;
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}

	public String getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}

	@Override
	public String toString() {
		return String.format("ErrorCode{code='%s', message='%s', httpStatusCode=%d}", code, message, httpStatusCode);
	}
}
