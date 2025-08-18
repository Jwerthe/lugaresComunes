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
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "route_proposals")
public class RouteProposal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by", nullable = false)
    @NotNull(message = "Usuario que propone es obligatorio")
    private User proposedBy;
    
    @Column(nullable = false)
    @NotBlank(message = "Título de la propuesta es obligatorio")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Descripción de la propuesta es obligatoria")
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
    
    @Column(name = "proposed_points", columnDefinition = "TEXT")
    private String proposedPoints; // JSON con array de coordenadas y descripciones
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProposalStatus status = ProposalStatus.PENDING;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Referencia a la ruta creada si se aprueba
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_route_id")
    private Route createdRoute;
    
    // Constructors
    public RouteProposal() {}
    
    public RouteProposal(User proposedBy, String title, String description, 
                        BigDecimal fromLatitude, BigDecimal fromLongitude, 
                        String fromDescription, Place toPlace) {
        this.proposedBy = proposedBy;
        this.title = title;
        this.description = description;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.fromDescription = fromDescription;
        this.toPlace = toPlace;
    }
    
    // Helper methods
    public void approve(User admin, String notes) {
        this.status = ProposalStatus.APPROVED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.adminNotes = notes;
    }
    
    public void reject(User admin, String notes) {
        this.status = ProposalStatus.REJECTED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.adminNotes = notes;
    }
    
    public boolean isPending() {
        return status == ProposalStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status == ProposalStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return status == ProposalStatus.REJECTED;
    }
    
    public String getGeneratedRouteName() {
        return fromDescription != null ? 
               fromDescription + " → " + toPlace.getName() :
               "Ruta a " + toPlace.getName();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getProposedBy() {
        return proposedBy;
    }
    
    public void setProposedBy(User proposedBy) {
        this.proposedBy = proposedBy;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public String getProposedPoints() {
        return proposedPoints;
    }
    
    public void setProposedPoints(String proposedPoints) {
        this.proposedPoints = proposedPoints;
    }
    
    public ProposalStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProposalStatus status) {
        this.status = status;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public User getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Route getCreatedRoute() {
        return createdRoute;
    }
    
    public void setCreatedRoute(Route createdRoute) {
        this.createdRoute = createdRoute;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteProposal)) return false;
        RouteProposal that = (RouteProposal) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}