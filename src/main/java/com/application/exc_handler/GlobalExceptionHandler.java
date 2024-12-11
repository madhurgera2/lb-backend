package com.application.exc_handler;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.application.constants.ErrorCode;
import com.application.custom_excs.DonorNotFoundException;
import com.application.custom_excs.EventNotFoundException;
import com.application.custom_excs.InvalidCredentialsException;
import com.application.custom_excs.InvalidTokenException;
import com.application.custom_excs.ResourceNotFoundException;
import com.application.custom_excs.RoleUpdateFailedException;
import com.application.custom_excs.UserAlreadyExistsException;
import com.application.custom_excs.UserNotFoundException;
import com.application.custom_excs.UserUpdateFailedException;
import com.application.model.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@Value("${spring.profiles.active:prod}")
	private String activeProfile;

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Helper method to create the ResponseEntity with ErrorResponse
	private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String message, HttpStatus status) {
		// Constructing the response with the appropriate error code, message, and
		// status
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), message, status.toString());
		return new ResponseEntity<>(errorResponse, status);
	}

	// Handle specific exceptions (User not found)
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
		logger.error("User not found: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.USER_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DonorNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleDonorNotFoundException(DonorNotFoundException ex, WebRequest request) {
	    logger.error("Donor not found: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
	    return createErrorResponse(ErrorCode.DONOR_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFoundException(EventNotFoundException ex, WebRequest request) {
        logger.error("Event not found: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
        return createErrorResponse(ErrorCode.EVENT_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
			WebRequest request) {
		logger.error("User already exists: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.USER_ALREADY_EXISTS, ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserUpdateFailedException.class)
	public ResponseEntity<ErrorResponse> handleUserUpdateFailedException(UserUpdateFailedException ex,
			WebRequest request) {
		logger.error("User update failed: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.USER_UPDATE_FAILED, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(RoleUpdateFailedException.class)
	public ResponseEntity<ErrorResponse> handleRoleUpdateFailedException(RoleUpdateFailedException ex,
			WebRequest request) {
		logger.error("Role update failed: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.ROLE_UPDATE_FAILED, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
		logger.warn("Invalid token for user: [masked] | Request URI: {}", request.getDescription(false));
		// Send a generic message in production
		String message = "prod".equalsIgnoreCase(activeProfile) ? "Invalid or expired token." : ex.getMessage();

		return createErrorResponse(ErrorCode.INVALID_TOKEN, message, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
		logger.warn("Expired JWT token: [masked] | Request URI: {}", ex.getMessage(), request.getDescription(false));
		String message = "prod".equalsIgnoreCase(activeProfile) ? "Your session has expired. Please log in again."
				: ex.getMessage();
		return createErrorResponse(ErrorCode.JWT_EXPIRED, message, HttpStatus.UNAUTHORIZED);
	}

	// Handle specific exceptions (Invalid credentials)
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex,
			WebRequest request) {
		logger.warn("Invalid credentials attempt: {} | Request URI: {}", ex.getMessage(),
				request.getDescription(false));
		return createErrorResponse(ErrorCode.INVALID_CREDENTIALS, ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	// Handle illegal argument exceptions
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
			WebRequest request) {
		logger.warn("Invalid input: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.INVALID_INPUT, ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	// Handle validation errors (e.g., @Valid annotation)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		logger.warn("Validation error: {} | URI: {}", ex.getMessage(), request.getDescription(false));

		// Collect detailed validation error messages
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> String.format("Field '%s': %s (Rejected value: '%s')", error.getField(),
						error.getDefaultMessage(), error.getRejectedValue()))
				.toList();

		String message = errors.isEmpty() ? "Validation failed for request." : String.join(", ", errors);
		return createErrorResponse(ErrorCode.VALIDATION_ERROR, message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
			WebRequest request) {
		logger.error("Validation error during persistence: {} | Request URI: {}", ex.getMessage(),
				request.getDescription(false));

		// Collect detailed validation error messages
		List<String> errors = ex.getConstraintViolations().stream().map(
				violation -> String.format("Property '%s': %s", violation.getPropertyPath(), violation.getMessage()))
				.toList();

		String message = errors.isEmpty() ? "Validation failed during persistence." : String.join(", ", errors);
		return createErrorResponse(ErrorCode.CONSTRAINT_VIOLATION, message, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	// Handle Access Denied errors (Spring Security)
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
		logger.info("Access Denied: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.ACCESS_DENIED, "You do not have permission to access this resource",
				HttpStatus.FORBIDDEN);
	}

	// Handle NullPointerException
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, WebRequest request) {
		logger.error("NullPointerException occurred: {} | Request URI: {}", ex.getMessage(),
				request.getDescription(false));
		return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "Null Pointer Exception occurred.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle ResourceNotFoundException
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		logger.error("Resource not found: {} | Request URI: {}", ex.getMessage(), request.getDescription(false));
		return createErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	// Handle generic exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
		logger.error("An unexpected error occurred: {} | Request URI: {}", ex.getMessage(),
				request.getDescription(false));
		return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, "An unexpected error occurred.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
