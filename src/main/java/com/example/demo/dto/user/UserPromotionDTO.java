// UserPromotionDTO.java - CORREGIDO
package com.example.demo.dto.user;

import com.example.demo.entity.UserPromotion;
import com.example.demo.entity.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserPromotionDTO {
    
    private UUID id;
    private UserDTO user;
    private UserType fromUserType;
    private UserType toUserType;
    private UserDTO promotedBy;
    private String reason;
    private Integer contributionScoreAtPromotion;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime promotedAt;
    
    // Constructors
    public UserPromotionDTO() {}
    
    public UserPromotionDTO(UserPromotion promotion) {
        this.id = promotion.getId();
        this.user = UserDTO.fromEntity(promotion.getUser());
        this.fromUserType = promotion.getFromUserType();
        this.toUserType = promotion.getToUserType();
        this.promotedBy = UserDTO.fromEntity(promotion.getPromotedBy());
        this.reason = promotion.getReason();
        this.contributionScoreAtPromotion = promotion.getContributionScoreAtPromotion();
        this.promotedAt = promotion.getPromotedAt();
    }
    
    public static UserPromotionDTO fromEntity(UserPromotion promotion) {
        return new UserPromotionDTO(promotion);
    }
    
    // Helper methods
    public String getPromotionDescription() {
        return String.format("%s → %s", 
               fromUserType.getDisplayName(), 
               toUserType.getDisplayName());
    }
    
    public boolean isUpgrade() {
        return toUserType == UserType.ADMIN && fromUserType != UserType.ADMIN;
    }
    
    // 🔧 FIX CRÍTICO: Manejar promotedAt null
    public Long getDaysAgo() {
        if (promotedAt == null) {
            return null; // O retorna 0L si prefieres
        }
        return java.time.temporal.ChronoUnit.DAYS.between(
            promotedAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        );
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    
    public UserType getFromUserType() { return fromUserType; }
    public void setFromUserType(UserType fromUserType) { this.fromUserType = fromUserType; }
    
    public UserType getToUserType() { return toUserType; }
    public void setToUserType(UserType toUserType) { this.toUserType = toUserType; }
    
    public UserDTO getPromotedBy() { return promotedBy; }
    public void setPromotedBy(UserDTO promotedBy) { this.promotedBy = promotedBy; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Integer getContributionScoreAtPromotion() { return contributionScoreAtPromotion; }
    public void setContributionScoreAtPromotion(Integer contributionScoreAtPromotion) { this.contributionScoreAtPromotion = contributionScoreAtPromotion; }
    
    public LocalDateTime getPromotedAt() { return promotedAt; }
    public void setPromotedAt(LocalDateTime promotedAt) { this.promotedAt = promotedAt; }
}