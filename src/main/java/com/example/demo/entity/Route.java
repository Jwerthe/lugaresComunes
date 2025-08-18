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
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    @NotBlank(message = "Nombre de la ruta es obligatorio")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "from_latitude", nullable = false)
    @NotNull(message = "Latitud de inicio es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal fromLatitude;
    
    @Column(name = "from_longitude", nullable = false)
    @NotNull(message = "Longitud de inicio es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal fromLongitude;
    
    @Column(name = "from_description")
    private String fromDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_place_id", nullable = false)
    @NotNull(message = "Lugar de destino es obligatorio")
    private Place toPlace;
    
    @Column(name = "total_distance")
    private Integer totalDistance; // metros
    
    @Column(name = "estimated_time")
    private Integer estimatedTime; // minutos
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private RouteDifficulty difficulty = RouteDifficulty.EASY;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @NotNull(message = "Usuario creador es obligatorio")
    private User createdBy;
    
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "total_ratings")
    private Integer totalRatings = 0;
    
    @Column(name = "times_used")
    private Integer timesUsed = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relaciones
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<RoutePoint> routePoints = new ArrayList<>();
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RouteRating> ratings = new HashSet<>();
    
    @OneToMany(mappedBy = "routeUsed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<NavigationHistory> navigationHistory = new HashSet<>();
    
    // Constructors
    public Route() {}
    
    public Route(String name, String description, BigDecimal fromLatitude, 
                 BigDecimal fromLongitude, String fromDescription, Place toPlace, User createdBy) {
        this.name = name;
        this.description = description;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.fromDescription = fromDescription;
        this.toPlace = toPlace;
        this.createdBy = createdBy;
    }
    
    // Helper methods
    public void updateAverageRating() {
        if (totalRatings == 0) {
            this.averageRating = 0.0;
            return;
        }
        
        double sum = ratings.stream()
                .mapToInt(RouteRating::getRating)
                .sum();
        this.averageRating = Math.round((sum / totalRatings) * 100.0) / 100.0;
    }
    
    public void incrementUsage() {
        this.timesUsed++;
    }
    
    public void addRating(RouteRating rating) {
        this.ratings.add(rating);
        this.totalRatings = this.ratings.size();
        updateAverageRating();
    }
    
    public void removeRating(RouteRating rating) {
        this.ratings.remove(rating);
        this.totalRatings = this.ratings.size();
        updateAverageRating();
    }
    
    // Getters and Setters
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getFromLatitude() {
        return fromLatitude;
    }
    
    public void setFromLatitude(BigDecimal fromLatitude) {
        this.fromLatitude = fromLatitude;
    }
    
    public BigDecimal getFromLongitude() {
        return fromLongitude;
    }
    
    public void setFromLongitude(BigDecimal fromLongitude) {
        this.fromLongitude = fromLongitude;
    }
    
    public String getFromDescription() {
        return fromDescription;
    }
    
    public void setFromDescription(String fromDescription) {
        this.fromDescription = fromDescription;
    }
    
    public Place getToPlace() {
        return toPlace;
    }
    
    public void setToPlace(Place toPlace) {
        this.toPlace = toPlace;
    }
    
    public Integer getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public RouteDifficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(RouteDifficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getTotalRatings() {
        return totalRatings;
    }
    
    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
    
    public Integer getTimesUsed() {
        return timesUsed;
    }
    
    public void setTimesUsed(Integer timesUsed) {
        this.timesUsed = timesUsed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }
    
    public void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }
    
    public Set<RouteRating> getRatings() {
        return ratings;
    }
    
    public void setRatings(Set<RouteRating> ratings) {
        this.ratings = ratings;
    }
    
    public Set<NavigationHistory> getNavigationHistory() {
        return navigationHistory;
    }
    
    public void setNavigationHistory(Set<NavigationHistory> navigationHistory) {
        this.navigationHistory = navigationHistory;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}