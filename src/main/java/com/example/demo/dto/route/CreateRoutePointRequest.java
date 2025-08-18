// CreateRoutePointRequest.java
package com.example.demo.dto.route;

import com.example.demo.entity.RoutePointType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreateRoutePointRequest {
    
    @NotNull(message = "Latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal longitude;
    
    @NotNull(message = "Orden es obligatorio")
    @Min(value = 0, message = "Orden debe ser mayor o igual a 0")
    private Integer orderIndex;
    
    @NotNull(message = "Tipo de punto es obligatorio")
    private RoutePointType pointType;
    
    private String instruction;
    
    private String landmarkDescription;
    
    @Min(value = 0, message = "Distancia debe ser mayor o igual a 0")
    private Integer distanceFromPrevious;
    
    // Constructors, getters and setters...
    public CreateRoutePointRequest() {}
    
    // Getters and Setters
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
}