package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "route_ratings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "user_id"}))
public class RouteRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @NotNull(message = "Ruta es obligatoria")
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    private User user;
    
    @Column(nullable = false)
    @NotNull(message = "Calificación es obligatoria")
    @Min(value = 1, message = "Calificación mínima es 1 estrella")
    @Max(value = 5, message = "Calificación máxima es 5 estrellas")
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public RouteRating() {}
    
    public RouteRating(Route route, User user, Integer rating) {
        this.route = route;
        this.user = user;
        this.rating = rating;
    }
    
    public RouteRating(Route route, User user, Integer rating, String comment) {
        this(route, user, rating);
        this.comment = comment;
    }
    
    // Helper methods
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
    
    public String getRatingText() {
        return switch (rating) {
            case 1 -> "Muy mala";
            case 2 -> "Mala";
            case 3 -> "Regular";
            case 4 -> "Buena";
            case 5 -> "Excelente";
            default -> "Sin calificación";
        };
    }
    
    public String getStars() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
    
    public boolean isPositive() {
        return rating >= 4;
    }
    
    public boolean isNegative() {
        return rating <= 2;
    }
    
    public boolean isNeutral() {
        return rating == 3;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteRating)) return false;
        RouteRating that = (RouteRating) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}