// RouteProposalDTO.java
package com.example.demo.dto.route;

import com.example.demo.entity.RouteProposal;
import com.example.demo.entity.ProposalStatus;
import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.dto.user.UserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RouteProposalDTO {
    
    private UUID id;
    private UserDTO proposedBy;
    private String title;
    private String description;
    private BigDecimal fromLatitude;
    private BigDecimal fromLongitude;
    private String fromDescription;
    private PlaceDTO toPlace;
    private String proposedPoints; // JSON
    private ProposalStatus status;
    private String adminNotes;
    private UserDTO reviewedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private RouteDTO createdRoute; // Ruta creada si fue aprobada
    
    // Constructors
    public RouteProposalDTO() {}
    
    public RouteProposalDTO(RouteProposal proposal) {
        this.id = proposal.getId();
        this.proposedBy = UserDTO.fromEntity(proposal.getProposedBy());
        this.title = proposal.getTitle();
        this.description = proposal.getDescription();
        this.fromLatitude = proposal.getFromLatitude();
        this.fromLongitude = proposal.getFromLongitude();
        this.fromDescription = proposal.getFromDescription();
        this.toPlace = PlaceDTO.fromEntity(proposal.getToPlace());
        this.proposedPoints = proposal.getProposedPoints();
        this.status = proposal.getStatus();
        this.adminNotes = proposal.getAdminNotes();
        if (proposal.getReviewedBy() != null) {
            this.reviewedBy = UserDTO.fromEntity(proposal.getReviewedBy());
        }
        this.reviewedAt = proposal.getReviewedAt();
        this.createdAt = proposal.getCreatedAt();
        this.updatedAt = proposal.getUpdatedAt();
        if (proposal.getCreatedRoute() != null) {
            this.createdRoute = RouteDTO.fromEntity(proposal.getCreatedRoute());
        }
    }
    
    public static RouteProposalDTO fromEntity(RouteProposal proposal) {
        return new RouteProposalDTO(proposal);
    }
    
    // Helper methods
    public String getStatusText() {
        return status != null ? status.getDisplayName() : "Estado desconocido";
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
    
    public long getDaysSinceCreated() {
        return java.time.temporal.ChronoUnit.DAYS.between(
            createdAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        );
    }
    
    public boolean hasAdminNotes() {
        return adminNotes != null && !adminNotes.trim().isEmpty();
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UserDTO getProposedBy() { return proposedBy; }
    public void setProposedBy(UserDTO proposedBy) { this.proposedBy = proposedBy; }
    
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
    
    public PlaceDTO getToPlace() { return toPlace; }
    public void setToPlace(PlaceDTO toPlace) { this.toPlace = toPlace; }
    
    public String getProposedPoints() { return proposedPoints; }
    public void setProposedPoints(String proposedPoints) { this.proposedPoints = proposedPoints; }
    
    public ProposalStatus getStatus() { return status; }
    public void setStatus(ProposalStatus status) { this.status = status; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public UserDTO getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserDTO reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public RouteDTO getCreatedRoute() { return createdRoute; }
    public void setCreatedRoute(RouteDTO createdRoute) { this.createdRoute = createdRoute; }
}