// GlobalExceptionHandler.java - Actualizado con nuevas excepciones
package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo de excepciones específicas de rutas
    @ExceptionHandler(RouteValidationException.class)
    public ResponseEntity<?> handleRouteValidationException(RouteValidationException ex, WebRequest request) {
        logger.error("Route validation error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "ROUTE_VALIDATION_ERROR", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NavigationException.class)
    public ResponseEntity<?> handleNavigationException(NavigationException ex, WebRequest request) {
        logger.error("Navigation error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "NAVIGATION_ERROR", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProposalException.class)
    public ResponseEntity<?> handleProposalException(ProposalException ex, WebRequest request) {
        logger.error("Proposal error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "PROPOSAL_ERROR", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Excepciones existentes (Resource not found, bad request, etc.)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "RESOURCE_NOT_FOUND", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logger.error("Bad request: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "BAD_REQUEST", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "ACCESS_DENIED", 
            "No tienes permisos para realizar esta acción", 
            HttpStatus.FORBIDDEN
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = createErrorResponse(
            "VALIDATION_ERROR", 
            "Error de validación en los datos enviados", 
            HttpStatus.BAD_REQUEST
        );
        errorResponse.put("fieldErrors", fieldErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            "INTERNAL_SERVER_ERROR", 
            "Ha ocurrido un error interno. Por favor intenta de nuevo más tarde.", 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return errorResponse;
    }
}