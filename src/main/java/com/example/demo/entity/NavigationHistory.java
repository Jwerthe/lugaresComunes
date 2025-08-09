package com.example.demo.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "navigation_history")
public class NavigationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "from_lat", precision = 10, scale = 8)
    private BigDecimal fromLat;
    
    @Column(name = "from_lng", precision = 11, scale = 8)
    private BigDecimal fromLng;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_place_id", nullable = false)
    private Place toPlace;
    
    @Column(name = "navigation_started_at")
    private LocalDateTime navigationStartedAt;
    
    @Column(name = "navigation_completed_at")
    private LocalDateTime navigationCompletedAt;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public NavigationHistory() {}
    
    public NavigationHistory(User user, Place toPlace, BigDecimal fromLat, BigDecimal fromLng) {
        this.user = user;
        this.toPlace = toPlace;
        this.fromLat = fromLat;
        this.fromLng = fromLng;
        this.navigationStartedAt = LocalDateTime.now();
    }
    
    // Helper method to complete navigation
    public void completeNavigation() {
        this.navigationCompletedAt = LocalDateTime.now();
        if (this.navigationStartedAt != null) {
            this.durationSeconds = (int) java.time.Duration.between(
                this.navigationStartedAt, 
                this.navigationCompletedAt
            ).getSeconds();
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public BigDecimal getFromLat() {
        return fromLat;
    }
    
    public void setFromLat(BigDecimal fromLat) {
        this.fromLat = fromLat;
    }
    
    public BigDecimal getFromLng() {
        return fromLng;
    }
    
    public void setFromLng(BigDecimal fromLng) {
        this.fromLng = fromLng;
    }
    
    public Place getToPlace() {
        return toPlace;
    }
    
    public void setToPlace(Place toPlace) {
        this.toPlace = toPlace;
    }
    
    public LocalDateTime getNavigationStartedAt() {
        return navigationStartedAt;
    }
    
    public void setNavigationStartedAt(LocalDateTime navigationStartedAt) {
        this.navigationStartedAt = navigationStartedAt;
    }
    
    public LocalDateTime getNavigationCompletedAt() {
        return navigationCompletedAt;
    }
    
    public void setNavigationCompletedAt(LocalDateTime navigationCompletedAt) {
        this.navigationCompletedAt = navigationCompletedAt;
    }
    
    public Integer getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NavigationHistory)) return false;
        NavigationHistory that = (NavigationHistory) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}