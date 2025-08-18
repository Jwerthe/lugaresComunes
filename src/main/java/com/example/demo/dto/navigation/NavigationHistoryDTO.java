// NavigationHistoryDTO.java
package com.example.demo.dto.navigation;

import com.example.demo.entity.NavigationHistory;
import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.dto.route.RouteDTO;
import com.example.demo.dto.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class NavigationHistoryDTO {
    
    private UUID id;
    private UserDTO user;
    private BigDecimal fromLat;
    private BigDecimal fromLng;
    private PlaceDTO toPlace;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime navigationStartedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime navigationCompletedAt;
    
    private Integer durationSeconds;
    private RouteDTO routeUsed;
    private Boolean routeCompleted;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Constructors
    public NavigationHistoryDTO() {}
    
    public NavigationHistoryDTO(NavigationHistory navigation) {
        this.id = navigation.getId();
        this.user = UserDTO.fromEntity(navigation.getUser());
        this.fromLat = navigation.getFromLat();
        this.fromLng = navigation.getFromLng();
        this.toPlace = PlaceDTO.fromEntity(navigation.getToPlace());
        this.navigationStartedAt = navigation.getNavigationStartedAt();
        this.navigationCompletedAt = navigation.getNavigationCompletedAt();
        this.durationSeconds = navigation.getDurationSeconds();
        if (navigation.getRouteUsed() != null) {
            this.routeUsed = RouteDTO.fromEntity(navigation.getRouteUsed());
        }
        this.routeCompleted = navigation.getRouteCompleted();
        this.createdAt = navigation.getCreatedAt();
    }
    
    public static NavigationHistoryDTO fromEntity(NavigationHistory navigation) {
        return new NavigationHistoryDTO(navigation);
    }
    
    // Helper methods
    public boolean isCompleted() {
        return navigationCompletedAt != null;
    }
    
    public boolean hasUsedRoute() {
        return routeUsed != null;
    }
    
    public String getStatusText() {
        if (!isCompleted()) {
            return "En progreso";
        }
        
        if (hasUsedRoute()) {
            return routeCompleted ? "Completada con ruta" : "Ruta abandonada";
        }
        
        return "Completada (navegación libre)";
    }
    
    public String getFormattedDuration() {
        if (durationSeconds == null) return "Duración no disponible";
        
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        
        if (minutes == 0) {
            return seconds + " segundos";
        } else if (minutes < 60) {
            return minutes + " minutos";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return String.format("%d:%02d horas", hours, remainingMinutes);
        }
    }
    
    public boolean canRateRoute() {
        return hasUsedRoute() && isCompleted() && routeCompleted;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    
    public BigDecimal getFromLat() { return fromLat; }
    public void setFromLat(BigDecimal fromLat) { this.fromLat = fromLat; }
    
    public BigDecimal getFromLng() { return fromLng; }
    public void setFromLng(BigDecimal fromLng) { this.fromLng = fromLng; }
    
    public PlaceDTO getToPlace() { return toPlace; }
    public void setToPlace(PlaceDTO toPlace) { this.toPlace = toPlace; }
    
    public LocalDateTime getNavigationStartedAt() { return navigationStartedAt; }
    public void setNavigationStartedAt(LocalDateTime navigationStartedAt) { this.navigationStartedAt = navigationStartedAt; }
    
    public LocalDateTime getNavigationCompletedAt() { return navigationCompletedAt; }
    public void setNavigationCompletedAt(LocalDateTime navigationCompletedAt) { this.navigationCompletedAt = navigationCompletedAt; }
    
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public RouteDTO getRouteUsed() { return routeUsed; }
    public void setRouteUsed(RouteDTO routeUsed) { this.routeUsed = routeUsed; }
    
    public Boolean getRouteCompleted() { return routeCompleted; }
    public void setRouteCompleted(Boolean routeCompleted) { this.routeCompleted = routeCompleted; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}