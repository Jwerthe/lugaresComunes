// NavigationStartRequest.java
package com.example.demo.dto.navigation;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class NavigationStartRequest {
    
    @NotNull(message = "Latitud de inicio es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal fromLatitude;
    
    @NotNull(message = "Longitud de inicio es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal fromLongitude;
    
    @NotNull(message = "Lugar de destino es obligatorio")
    private UUID toPlaceId;
    
    // Opcional: si el usuario seleccionó una ruta específica
    private UUID routeId;
    
    // Constructors, getters and setters...
    public NavigationStartRequest() {}
    
    public BigDecimal getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(BigDecimal fromLatitude) { this.fromLatitude = fromLatitude; }
    
    public BigDecimal getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(BigDecimal fromLongitude) { this.fromLongitude = fromLongitude; }
    
    public UUID getToPlaceId() { return toPlaceId; }
    public void setToPlaceId(UUID toPlaceId) { this.toPlaceId = toPlaceId; }
    
    public UUID getRouteId() { return routeId; }
    public void setRouteId(UUID routeId) { this.routeId = routeId; }
}