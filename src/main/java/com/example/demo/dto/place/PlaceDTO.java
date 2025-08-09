package com.example.demo.dto.place;


import com.example.demo.entity.Place;
import com.example.demo.entity.PlaceType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class PlaceDTO {
 
 private UUID id;
 private String name;
 private String category;
 private String description;
 private String what3words;
 private BigDecimal latitude;
 private BigDecimal longitude;
 private Boolean isAvailable;
 private PlaceType placeType;
 private Integer capacity;
 private String schedule;
 private String imageUrl;
 private String buildingName;
 private Integer floorNumber;
 private String roomCode;
 private Set<String> equipment;
 private Set<String> accessibilityFeatures;
 
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 private LocalDateTime createdAt;
 
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 private LocalDateTime updatedAt;
 
 // Constructors
 public PlaceDTO() {}
 
 public PlaceDTO(Place place) {
     this.id = place.getId();
     this.name = place.getName();
     this.category = place.getCategory();
     this.description = place.getDescription();
     this.what3words = place.getWhat3words();
     this.latitude = place.getLatitude();
     this.longitude = place.getLongitude();
     this.isAvailable = place.getIsAvailable();
     this.placeType = place.getPlaceType();
     this.capacity = place.getCapacity();
     this.schedule = place.getSchedule();
     this.imageUrl = place.getImageUrl();
     this.buildingName = place.getBuildingName();
     this.floorNumber = place.getFloorNumber();
     this.roomCode = place.getRoomCode();
     this.equipment = place.getEquipment();
     this.accessibilityFeatures = place.getAccessibilityFeatures();
     this.createdAt = place.getCreatedAt();
     this.updatedAt = place.getUpdatedAt();
 }
 
 // Static factory method
 public static PlaceDTO fromEntity(Place place) {
     return new PlaceDTO(place);
 }
 
 // Getters and Setters (all standard getters/setters)
 public UUID getId() {
     return id;
 }
 
 public void setId(UUID id) {
     this.id = id;
 }
 
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
 
 public LocalDateTime getCreatedAt() {
     return createdAt;
 }
 
 public void setCreatedAt(LocalDateTime createdAt) {
     this.createdAt = createdAt;
 }
 
 public LocalDateTime getUpdatedAt() {
     return updatedAt;
 }
 
 public void setUpdatedAt(LocalDateTime updatedAt) {
     this.updatedAt = updatedAt;
 }
}