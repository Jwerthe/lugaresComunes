package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_promotions")
public class UserPromotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Usuario es obligatorio")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "from_user_type", nullable = false)
    @NotNull(message = "Tipo de usuario anterior es obligatorio")
    private UserType fromUserType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "to_user_type", nullable = false)
    @NotNull(message = "Tipo de usuario nuevo es obligatorio")
    private UserType toUserType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promoted_by", nullable = false)
    @NotNull(message = "Usuario que promociona es obligatorio")
    private User promotedBy;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Razón de la promoción es obligatoria")
    private String reason;
    
    @Column(name = "contribution_score_at_promotion")
    private Integer contributionScoreAtPromotion;
    
    @CreationTimestamp
    @Column(name = "promoted_at", nullable = false, updatable = false)
    private LocalDateTime promotedAt;
    
    // Constructors
    public UserPromotion() {}
    
    public UserPromotion(User user, UserType fromUserType, UserType toUserType, 
                        User promotedBy, String reason) {
        this.user = user;
        this.fromUserType = fromUserType;
        this.toUserType = toUserType;
        this.promotedBy = promotedBy;
        this.reason = reason;
        this.contributionScoreAtPromotion = user.getContributionScore();
    }
    
    // Helper methods
    public boolean isUpgrade() {
        // Consideramos que ADMIN es el nivel más alto
        return toUserType == UserType.ADMIN && fromUserType != UserType.ADMIN;
    }
    
    public boolean isDowngrade() {
        // Consideramos que VISITOR es el nivel más bajo
        return toUserType == UserType.VISITOR && fromUserType != UserType.VISITOR;
    }
    
    public String getPromotionDescription() {
        if (isUpgrade()) {
            return String.format("Promoción de %s a %s", 
                   fromUserType.getDisplayName(), toUserType.getDisplayName());
        } else if (isDowngrade()) {
            return String.format("Degradación de %s a %s", 
                   fromUserType.getDisplayName(), toUserType.getDisplayName());
        } else {
            return String.format("Cambio de %s a %s", 
                   fromUserType.getDisplayName(), toUserType.getDisplayName());
        }
    }
    
    public String getFormattedDate() {
        return promotedAt.toLocalDate().toString();
    }
    
    public long getDaysAgo() {
        return java.time.temporal.ChronoUnit.DAYS.between(
            promotedAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        );
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
    
    public UserType getFromUserType() {
        return fromUserType;
    }
    
    public void setFromUserType(UserType fromUserType) {
        this.fromUserType = fromUserType;
    }
    
    public UserType getToUserType() {
        return toUserType;
    }
    
    public void setToUserType(UserType toUserType) {
        this.toUserType = toUserType;
    }
    
    public User getPromotedBy() {
        return promotedBy;
    }
    
    public void setPromotedBy(User promotedBy) {
        this.promotedBy = promotedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Integer getContributionScoreAtPromotion() {
        return contributionScoreAtPromotion;
    }
    
    public void setContributionScoreAtPromotion(Integer contributionScoreAtPromotion) {
        this.contributionScoreAtPromotion = contributionScoreAtPromotion;
    }
    
    public LocalDateTime getPromotedAt() {
        return promotedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPromotion)) return false;
        UserPromotion that = (UserPromotion) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}