// NavigationCompleteRequest.java
package com.example.demo.dto.navigation;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NavigationCompleteRequest {
    
    @NotNull(message = "ID de navegación es obligatorio")
    private UUID navigationId;
    
    // Si siguió la ruta hasta el final
    private Boolean routeCompleted;
    
    // Constructors, getters and setters...
    public NavigationCompleteRequest() {}
    
    public UUID getNavigationId() { return navigationId; }
    public void setNavigationId(UUID navigationId) { this.navigationId = navigationId; }
    
    public Boolean getRouteCompleted() { return routeCompleted; }
    public void setRouteCompleted(Boolean routeCompleted) { this.routeCompleted = routeCompleted; }
}