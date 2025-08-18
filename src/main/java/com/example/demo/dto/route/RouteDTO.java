// RouteDTO.java
package com.example.demo.dto.route;

import com.example.demo.entity.Route;
import com.example.demo.entity.RouteDifficulty;
import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.dto.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RouteDTO {
    
    private UUID id;
    private String name;
    private String description;
    private BigDecimal fromLatitude;
    private BigDecimal fromLongitude;
    private String fromDescription;
    private PlaceDTO toPlace;
    private Integer totalDistance;
    private Integer estimatedTime;
    private RouteDifficulty difficulty;
    private Boolean isActive;
    private UserDTO createdBy;
    private Double averageRating;
    private Integer totalRatings;
    private Integer timesUsed;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Lista de puntos de la ruta (opcional, solo cuando se solicite detalle completo)
    private List<RoutePointDTO> routePoints;
    
    // Constructors
    public RouteDTO() {}
    
    public RouteDTO(Route route) {
        this.id = route.getId();
        this.name = route.getName();
        this.description = route.getDescription();
        this.fromLatitude = route.getFromLatitude();
        this.fromLongitude = route.getFromLongitude();
        this.fromDescription = route.getFromDescription();
        this.toPlace = PlaceDTO.fromEntity(route.getToPlace());
        this.totalDistance = route.getTotalDistance();
        this.estimatedTime = route.getEstimatedTime();
        this.difficulty = route.getDifficulty();
        this.isActive = route.getIsActive();
        this.createdBy = UserDTO.fromEntity(route.getCreatedBy());
        this.averageRating = route.getAverageRating();
        this.totalRatings = route.getTotalRatings();
        this.timesUsed = route.getTimesUsed();
        this.createdAt = route.getCreatedAt();
        this.updatedAt = route.getUpdatedAt();
    }
    
    // Static factory method
    public static RouteDTO fromEntity(Route route) {
        return new RouteDTO(route);
    }
    
    // Factory method with route points
    public static RouteDTO fromEntityWithPoints(Route route) {
        RouteDTO dto = new RouteDTO(route);
        dto.routePoints = route.getRoutePoints().stream()
                .map(RoutePointDTO::fromEntity)
                .toList();
        return dto;
    }
    
    // Helper methods
    public String getDifficultyText() {
        return difficulty != null ? difficulty.getDisplayName() : "No especificada";
    }
    
    public String getFormattedDistance() {
        if (totalDistance == null) return "Distancia no calculada";
        if (totalDistance < 1000) {
            return totalDistance + " metros";
        } else {
            return String.format("%.1f km", totalDistance / 1000.0);
        }
    }
    
    public String getFormattedTime() {
        if (estimatedTime == null) return "Tiempo no estimado";
        if (estimatedTime < 60) {
            return estimatedTime + " minutos";
        } else {
            int hours = estimatedTime / 60;
            int minutes = estimatedTime % 60;
            return String.format("%d:%02d horas", hours, minutes);
        }
    }
    
    public String getRatingText() {
        if (totalRatings == 0) return "Sin calificaciones";
        return String.format("%.1f/5.0 (%d calificaciones)", averageRating, totalRatings);
    }
    
    public boolean isPopular() {
        return timesUsed >= 10; // Considerada popular si se ha usado 10+ veces
    }
    
    public boolean isWellRated() {
        return averageRating >= 4.0 && totalRatings >= 3;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
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
    
    public PlaceDTO getToPlace() { return toPlace; }
    public void setToPlace(PlaceDTO toPlace) { this.toPlace = toPlace; }
    
    public Integer getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Integer totalDistance) { this.totalDistance = totalDistance; }
    
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public RouteDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(RouteDifficulty difficulty) { this.difficulty = difficulty; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public UserDTO getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserDTO createdBy) { this.createdBy = createdBy; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }
    
    public Integer getTimesUsed() { return timesUsed; }
    public void setTimesUsed(Integer timesUsed) { this.timesUsed = timesUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<RoutePointDTO> getRoutePoints() { return routePoints; }
    public void setRoutePoints(List<RoutePointDTO> routePoints) { this.routePoints = routePoints; }
}