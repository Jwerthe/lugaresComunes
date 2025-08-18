// CreateRouteRequest.java
package com.example.demo.dto.route;

import com.example.demo.entity.RouteDifficulty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateRouteRequest {
    
    @NotBlank(message = "Nombre de la ruta es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "Latitud de inicio es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal fromLatitude;
    
    @NotNull(message = "Longitud de inicio es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal fromLongitude;
    
    private String fromDescription;
    
    @NotNull(message = "Lugar de destino es obligatorio")
    private UUID toPlaceId;
    
    @Min(value = 1, message = "Distancia debe ser mayor a 0")
    private Integer totalDistance;
    
    @Min(value = 1, message = "Tiempo estimado debe ser mayor a 0")
    private Integer estimatedTime;
    
    private RouteDifficulty difficulty = RouteDifficulty.EASY;
    
    private Boolean isActive = true;
    
    // Lista de puntos de la ruta
    @NotEmpty(message = "La ruta debe tener al menos un punto")
    private List<CreateRoutePointRequest> routePoints;
    
    // Constructors, getters and setters...
    public CreateRouteRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(BigDecimal fromLatitude) { this.fromLatitude = fromLatitude; }
    
    public BigDecimal getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(BigDecimal fromLongitude) { this.fromLongitude = fromLongitude; }
    
    public String getFromDescription() { return fromDescription; }
    public void setFromDescription(String fromDescription) { this.fromDescription = fromDescription; }
    
    public UUID getToPlaceId() { return toPlaceId; }
    public void setToPlaceId(UUID toPlaceId) { this.toPlaceId = toPlaceId; }
    
    public Integer getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Integer totalDistance) { this.totalDistance = totalDistance; }
    
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public RouteDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(RouteDifficulty difficulty) { this.difficulty = difficulty; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<CreateRoutePointRequest> getRoutePoints() { return routePoints; }
    public void setRoutePoints(List<CreateRoutePointRequest> routePoints) { this.routePoints = routePoints; }
}