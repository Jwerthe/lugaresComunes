// RouteValidationException.java
package com.example.demo.exception;

public class RouteValidationException extends BadRequestException {
    
    public RouteValidationException(String message) {
        super("Error de validación de ruta: " + message);
    }
    
    public RouteValidationException(String field, String issue) {
        super(String.format("Error en campo '%s': %s", field, issue));
    }
}