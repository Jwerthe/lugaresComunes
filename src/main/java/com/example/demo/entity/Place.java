package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "places")
public class Place {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre es obligatorio")
    private String name;
    
    @Column(nullable = false)
    @NotBlank(message = "Categoría es obligatoria")
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "what3words")
    private String what3words;
    
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
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false)
    private PlaceType placeType;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "schedule")
    private String schedule;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "building_name")
    private String buildingName;
    
    @Column(name = "floor_number")
    private Integer floorNumber;
    
    @Column(name = "room_code")
    private String roomCode;
    
    @ElementCollection
    @CollectionTable(name = "place_equipment", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "equipment_item")
    private Set<String> equipment = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "place_accessibility_features", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "accessibility_feature")
    private Set<String> accessibilityFeatures = new HashSet<>();
    
    // 🆕 NUEVOS CAMPOS PARA SISTEMA DE RUTAS
    @Column(name = "is_route_destination")
    private Boolean isRouteDestination = true; // Si puede ser destino de rutas
    
    @Column(name = "route_count", nullable = false)
    private Integer routeCount = 0; // Cantidad de rutas que llegan aquí
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relaciones existentes
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFavorite> favoriteByUsers = new HashSet<>();
    
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PlaceReport> reports = new HashSet<>();
    
    @OneToMany(mappedBy = "toPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<NavigationHistory> navigationHistory = new HashSet<>();
    
    // 🆕 NUEVAS RELACIONES
    @OneToMany(mappedBy = "toPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Route> routesToThisPlace = new HashSet<>();
    
    @OneToMany(mappedBy = "toPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RouteProposal> proposalsToThisPlace = new HashSet<>();
    
    // Constructors
    public Place() {}
    
    public Place(String name, String category, PlaceType placeType, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.category = category;
        this.placeType = placeType;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // 🆕 NUEVOS MÉTODOS PARA RUTAS
    public void incrementRouteCount() {
        if (this.routeCount == null) this.routeCount = 0;
        this.routeCount = this.routeCount + 1;
    }
    
    public void decrementRouteCount() {
        this.routeCount = Math.max(0, this.routeCount - 1);
    }
    
    public boolean hasActiveRoutes() {
        return routesToThisPlace.stream().anyMatch(Route::getIsActive);
    }
    
    public List<Route> getActiveRoutes() {
        return routesToThisPlace.stream()
                .filter(Route::getIsActive)
                .sorted((r1, r2) -> r2.getAverageRating().compareTo(r1.getAverageRating()))
                .toList();
    }
    
    public int getPendingProposalsCount() {
        return (int) proposalsToThisPlace.stream()
                .filter(RouteProposal::isPending)
                .count();
    }
    
    public boolean isPopularDestination() {
        return routeCount >= 3; // Considerado popular si tiene 3+ rutas
    }
    
    public void enableAsRouteDestination() {
        this.isRouteDestination = true;
    }
    
    public void disableAsRouteDestination() {
        this.isRouteDestination = false;
    }
    
    // Getters and Setters (todos los existentes + nuevos campos)
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
    
    // 🆕 GETTERS/SETTERS PARA NUEVOS CAMPOS
    public Boolean getIsRouteDestination() {
        return isRouteDestination;
    }
    
    public void setIsRouteDestination(Boolean isRouteDestination) {
        this.isRouteDestination = isRouteDestination;
    }
    
    public Integer getRouteCount() {
        return routeCount;
    }
    
    public void setRouteCount(Integer routeCount) {
        this.routeCount = routeCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Getters/Setters para relaciones existentes
    public Set<UserFavorite> getFavoriteByUsers() {
        return favoriteByUsers;
    }
    
    public void setFavoriteByUsers(Set<UserFavorite> favoriteByUsers) {
        this.favoriteByUsers = favoriteByUsers;
    }
    
    public Set<PlaceReport> getReports() {
        return reports;
    }
    
    public void setReports(Set<PlaceReport> reports) {
        this.reports = reports;
    }
    
    public Set<NavigationHistory> getNavigationHistory() {
        return navigationHistory;
    }
    
    public void setNavigationHistory(Set<NavigationHistory> navigationHistory) {
        this.navigationHistory = navigationHistory;
    }
    
    // 🆕 GETTERS/SETTERS PARA NUEVAS RELACIONES
    public Set<Route> getRoutesToThisPlace() {
        return routesToThisPlace;
    }
    
    public void setRoutesToThisPlace(Set<Route> routesToThisPlace) {
        this.routesToThisPlace = routesToThisPlace;
    }
    
    public Set<RouteProposal> getProposalsToThisPlace() {
        return proposalsToThisPlace;
    }
    
    public void setProposalsToThisPlace(Set<RouteProposal> proposalsToThisPlace) {
        this.proposalsToThisPlace = proposalsToThisPlace;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return Objects.equals(id, place.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}