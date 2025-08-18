// CreateRouteProposalRequest.java
package com.example.demo.dto.route;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateRouteProposalRequest {
    
    @NotBlank(message = "Título de la propuesta es obligatorio")
    @Size(max = 100, message = "Título no puede exceder 100 caracteres")
    private String title;
    
    @NotBlank(message = "Descripción de la propuesta es obligatoria")
    @Size(max = 1000, message = "Descripción no puede exceder 1000 caracteres")
    private String description;
    
    @NotNull(message = "Latitud de inicio es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud debe ser mayor a -90")
    @DecimalMax(value = "90.0", message = "Latitud debe ser menor a 90")
    private BigDecimal fromLatitude;
    
    @NotNull(message = "Longitud de inicio es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud debe ser mayor a -180")
    @DecimalMax(value = "180.0", message = "Longitud debe ser menor a 180")
    private BigDecimal fromLongitude;
    
    @Size(max = 200, message = "Descripción de origen no puede exceder 200 caracteres")
    private String fromDescription;
    
    @NotNull(message = "Lugar de destino es obligatorio")
    private UUID toPlaceId;
    
    // JSON con array de coordenadas y descripciones propuestas
    private String proposedPoints;
    
    // Constructors, getters and setters...
    public CreateRouteProposalRequest() {}
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(BigDecimal fromLatitude) { this.fromLatitude = fromLatitude; }
    
    public BigDecimal getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(BigDecimal fromLongitude) { this.fromLongitude = fromLongitude; }
    
    public String getFromDescription() { return fromDescription; }
    public void setFromDescription(String fromDescription) { this.fromDescription = fromDescription; }
    
    public UUID getToPlaceId() { return toPlaceId; }
    public void setToPlaceId(UUID toPlaceId) { this.toPlaceId = toPlaceId; }
    
    public String getProposedPoints() { return proposedPoints; }
    public void setProposedPoints(String proposedPoints) { this.proposedPoints = proposedPoints; }
}