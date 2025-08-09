package com.example.demo.dto.place;

import com.example.demo.entity.PlaceType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Set;

public class UpdatePlaceRequest {
    
    private String name;
    private String category;
    private String description;
    private String what3words;
    
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal latitude;
    
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal longitude;
    
    private Boolean isAvailable;
    private PlaceType placeType;
    
    @Min(value = 1, message = "Capacidad debe ser mayor a 0")
    private Integer capacity;
    
    private String schedule;
    private String imageUrl;
    private String buildingName;
    
    @Min(value = 1, message = "Número de piso debe ser mayor a 0")
    private Integer floorNumber;
    
    private String roomCode;
    private Set<String> equipment;
    private Set<String> accessibilityFeatures;
    
    // Constructors
    public UpdatePlaceRequest() {}
    
    // Getters and Setters (same as CreatePlaceRequest but all fields are optional)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getWhat3words() {
        return what3words;
    }
    
    public void setWhat3words(String what3words) {
        this.what3words = what3words;
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
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public PlaceType getPlaceType() {
        return placeType;
    }
    
    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    
    public Integer getFloorNumber() {
        return floorNumber;
    }
    
    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }
    
    public String getRoomCode() {
        return roomCode;
    }
    
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    
    public Set<String> getEquipment() {
        return equipment;
    }
    
    public void setEquipment(Set<String> equipment) {
        this.equipment = equipment;
    }
    
    public Set<String> getAccessibilityFeatures() {
        return accessibilityFeatures;
    }
    
    public void setAccessibilityFeatures(Set<String> accessibilityFeatures) {
        this.accessibilityFeatures = accessibilityFeatures;
    }
}