// NavigationException.java
package com.example.demo.exception;

public class NavigationException extends BadRequestException {
    
    public NavigationException(String message) {
        super("Error de navegación: " + message);
    }
    
    public static NavigationException navigationNotFound(String navigationId) {
        return new NavigationException("No se encontró la navegación con ID: " + navigationId);
    }
    
    public static NavigationException navigationAlreadyCompleted() {
        return new NavigationException("Esta navegación ya fue completada anteriormente");
    }
    
    public static NavigationException unauthorizedNavigation() {
        return new NavigationException("No tienes permiso para completar esta navegación");
    }
}