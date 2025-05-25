package com.example.contractmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	  @ExceptionHandler(AccessDeniedException.class)
	    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
	        Map<String, Object> error = new HashMap<>();
	        error.put("error", "Unauthorized");
	        error.put("message", ex.getMessage());
	        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	    }

	    @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
	        Map<String, Object> error = new HashMap<>();
	        error.put("error", "Internal Server Error");
	        error.put("message", ex.getMessage());
	        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

}
