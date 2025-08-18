// RoutePointDTO.java
package com.example.demo.dto.route;

import com.example.demo.entity.RoutePoint;
import com.example.demo.entity.RoutePointType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RoutePointDTO {
    
    private UUID id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer orderIndex;
    private RoutePointType pointType;
    private String instruction;
    private String landmarkDescription;
    private Integer distanceFromPrevious;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Constructors
    public RoutePointDTO() {}
    
    public RoutePointDTO(RoutePoint routePoint) {
        this.id = routePoint.getId();
        this.latitude = routePoint.getLatitude();
        this.longitude = routePoint.getLongitude();
        this.orderIndex = routePoint.getOrderIndex();
        this.pointType = routePoint.getPointType();
        this.instruction = routePoint.getInstruction();
        this.landmarkDescription = routePoint.getLandmarkDescription();
        this.distanceFromPrevious = routePoint.getDistanceFromPrevious();
        this.createdAt = routePoint.getCreatedAt();
    }
    
    public static RoutePointDTO fromEntity(RoutePoint routePoint) {
        return new RoutePointDTO(routePoint);
    }
    
    // Helper methods
    public String getPointTypeText() {
        return pointType != null ? pointType.getDisplayName() : "Tipo no especificado";
    }
    
    public String getFormattedInstruction() {
        if (instruction != null && !instruction.trim().isEmpty()) {
            return instruction;
        }
        return getDefaultInstruction();
    }
    
    private String getDefaultInstruction() {
        return switch (pointType) {
            case START -> "Punto de inicio de la ruta";
            case WAYPOINT -> "Continúa por este punto";
            case TURN -> "Gira en este punto";
            case LANDMARK -> landmarkDescription != null ? 
                           "Punto de referencia: " + landmarkDescription : 
                           "Punto de referencia";
            case END -> "Has llegado a tu destino";
        };
    }
    
    public String getFormattedDistance() {
        if (distanceFromPrevious == null) return "";
        if (distanceFromPrevious < 1000) {
            return distanceFromPrevious + "m";
        } else {
            return String.format("%.1f km", distanceFromPrevious / 1000.0);
        }
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    
    public RoutePointType getPointType() { return pointType; }
    public void setPointType(RoutePointType pointType) { this.pointType = pointType; }
    
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    
    public String getLandmarkDescription() { return landmarkDescription; }
    public void setLandmarkDescription(String landmarkDescription) { this.landmarkDescription = landmarkDescription; }
    
    public Integer getDistanceFromPrevious() { return distanceFromPrevious; }
    public void setDistanceFromPrevious(Integer distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}