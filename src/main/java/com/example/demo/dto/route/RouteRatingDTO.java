// RouteRatingDTO.java
package com.example.demo.dto.route;

import com.example.demo.entity.RouteRating;
import com.example.demo.dto.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class RouteRatingDTO {
    
    private UUID id;
    private UserDTO user;
    private Integer rating;
    private String comment;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructors
    public RouteRatingDTO() {}
    
    public RouteRatingDTO(RouteRating routeRating) {
        this.id = routeRating.getId();
        this.user = UserDTO.fromEntity(routeRating.getUser());
        this.rating = routeRating.getRating();
        this.comment = routeRating.getComment();
        this.createdAt = routeRating.getCreatedAt();
        this.updatedAt = routeRating.getUpdatedAt();
    }
    
    public static RouteRatingDTO fromEntity(RouteRating routeRating) {
        return new RouteRatingDTO(routeRating);
    }
    
    // Helper methods
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
    
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
    
    public boolean isPositive() {
        return rating >= 4;
    }
    
    public boolean isNegative() {
        return rating <= 2;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}