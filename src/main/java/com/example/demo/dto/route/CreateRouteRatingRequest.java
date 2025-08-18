// CreateRouteRatingRequest.java
package com.example.demo.dto.route;

import jakarta.validation.constraints.*;

public class CreateRouteRatingRequest {
    
    @NotNull(message = "Calificación es obligatoria")
    @Min(value = 1, message = "Calificación mínima es 1 estrella")
    @Max(value = 5, message = "Calificación máxima es 5 estrellas")
    private Integer rating;
    
    @Size(max = 500, message = "Comentario no puede exceder 500 caracteres")
    private String comment;
    
    // Constructors, getters and setters...
    public CreateRouteRatingRequest() {}
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}