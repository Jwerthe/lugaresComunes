package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "route_points")
public class RoutePoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @NotNull(message = "Ruta es obligatoria")
    private Route route;
    
    @Column(nullable = false)
    @NotNull(message = "Latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal latitude;
    
    @Column(nullable = false)
    @NotNull(message = "Longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal longitude;
    
    @Column(name = "order_index", nullable = false)
    @NotNull(message = "Orden es obligatorio")
    private Integer orderIndex;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    @NotNull(message = "Tipo de punto es obligatorio")
    private RoutePointType pointType;
    
    @Column(columnDefinition = "TEXT")
    private String instruction;
    
    @Column(name = "landmark_description", columnDefinition = "TEXT")
    private String landmarkDescription;
    
    @Column(name = "distance_from_previous")
    private Integer distanceFromPrevious; // metros desde punto anterior
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public RoutePoint() {}
    
    public RoutePoint(Route route, BigDecimal latitude, BigDecimal longitude, 
                     Integer orderIndex, RoutePointType pointType) {
        this.route = route;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderIndex = orderIndex;
        this.pointType = pointType;
    }
    
    public RoutePoint(Route route, BigDecimal latitude, BigDecimal longitude, 
                     Integer orderIndex, RoutePointType pointType, String instruction) {
        this(route, latitude, longitude, orderIndex, pointType);
        this.instruction = instruction;
    }
    
    // Helper methods
    public String getFormattedInstruction() {
        if (instruction != null && !instruction.trim().isEmpty()) {
            return instruction;
        }
        
        // Generar instrucción básica basada en el tipo de punto
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
    
    public boolean isStartPoint() {
        return pointType == RoutePointType.START;
    }
    
    public boolean isEndPoint() {
        return pointType == RoutePointType.END;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public Integer getOrderIndex() {
        return orderIndex;
    }
    
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
    
    public RoutePointType getPointType() {
        return pointType;
    }
    
    public void setPointType(RoutePointType pointType) {
        this.pointType = pointType;
    }
    
    public String getInstruction() {
        return instruction;
    }
    
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    public String getLandmarkDescription() {
        return landmarkDescription;
    }
    
    public void setLandmarkDescription(String landmarkDescription) {
        this.landmarkDescription = landmarkDescription;
    }
    
    public Integer getDistanceFromPrevious() {
        return distanceFromPrevious;
    }
    
    public void setDistanceFromPrevious(Integer distanceFromPrevious) {
        this.distanceFromPrevious = distanceFromPrevious;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutePoint)) return false;
        RoutePoint that = (RoutePoint) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}