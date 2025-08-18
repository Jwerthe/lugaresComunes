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
    
    @Column(name = "from_lat")
    private BigDecimal fromLat;
    
    @Column(name = "from_lng")
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
    
    // 🆕 NUEVOS CAMPOS PARA SISTEMA DE RUTAS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_used_id")
    private Route routeUsed; // Qué ruta siguió el usuario
    
    @Column(name = "route_completed")
    private Boolean routeCompleted = false; // Si siguió la ruta hasta el final
    
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
    
    // 🆕 CONSTRUCTOR CON RUTA
    public NavigationHistory(User user, Place toPlace, BigDecimal fromLat, 
                           BigDecimal fromLng, Route routeUsed) {
        this(user, toPlace, fromLat, fromLng);
        this.routeUsed = routeUsed;
    }
    
    // Helper method to complete navigation (existente, mejorado)
    public void completeNavigation() {
        this.navigationCompletedAt = LocalDateTime.now();
        if (this.navigationStartedAt != null) {
            this.durationSeconds = (int) java.time.Duration.between(
                this.navigationStartedAt, 
                this.navigationCompletedAt
            ).getSeconds();
        }
        
        // 🆕 Si usó una ruta, marcar como completada por defecto
        if (this.routeUsed != null) {
            this.routeCompleted = true;
            // Incrementar el contador de uso de la ruta
            this.routeUsed.incrementUsage();
        }
    }
    
    // 🆕 NUEVOS MÉTODOS PARA RUTAS
    public void completeNavigationWithRoute(boolean followedRoute) {
        completeNavigation();
        this.routeCompleted = followedRoute;
    }
    
    public boolean hasUsedRoute() {
        return routeUsed != null;
    }
    
    public boolean isSuccessful() {
        return navigationCompletedAt != null;
    }
    
    public boolean canRateRoute() {
        // Solo puede calificar si completó la navegación usando una ruta
        return hasUsedRoute() && isSuccessful() && routeCompleted;
    }
    
    public String getNavigationSummary() {
        if (!isSuccessful()) {
            return "Navegación incompleta";
        }
        
        String summary = String.format("Navegación a %s", toPlace.getName());
        if (hasUsedRoute()) {
            summary += String.format(" usando ruta '%s'", routeUsed.getName());
            if (routeCompleted) {
                summary += " (ruta completada)";
            } else {
                summary += " (ruta abandonada)";
            }
        } else {
            summary += " (navegación libre)";
        }
        
        if (durationSeconds != null) {
            int minutes = durationSeconds / 60;
            summary += String.format(" - Duración: %d minutos", minutes);
        }
        
        return summary;
    }
    
    public long getNavigationDurationMinutes() {
        return durationSeconds != null ? durationSeconds / 60 : 0;
    }
    
    // Getters and Setters (todos los existentes + nuevos campos)
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
    
    // 🆕 GETTERS/SETTERS PARA NUEVOS CAMPOS
    public Route getRouteUsed() {
        return routeUsed;
    }
    
    public void setRouteUsed(Route routeUsed) {
        this.routeUsed = routeUsed;
    }
    
    public Boolean getRouteCompleted() {
        return routeCompleted;
    }
    
    public void setRouteCompleted(Boolean routeCompleted) {
        this.routeCompleted = routeCompleted;
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