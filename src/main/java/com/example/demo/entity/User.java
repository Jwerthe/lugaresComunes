package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es obligatorio")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Password es obligatorio")
    private String password;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "student_id")
    private String studentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType = UserType.VISITOR; // 🔄 Cambiado de STUDENT a VISITOR
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 🆕 NUEVO CAMPO: Puntuación de contribuciones
    @Column(name = "contribution_score")
    private Integer contributionScore = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relaciones existentes
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFavorite> favorites = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PlaceReport> reports = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<NavigationHistory> navigationHistory = new HashSet<>();
    
    // 🆕 NUEVAS RELACIONES
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Route> createdRoutes = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RouteRating> routeRatings = new HashSet<>();
    
    @OneToMany(mappedBy = "proposedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RouteProposal> routeProposals = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPromotion> promotions = new HashSet<>();
    
    @OneToMany(mappedBy = "promotedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPromotion> promotionsGiven = new HashSet<>();
    
    // Constructors
    public User() {}
    
    public User(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }
    
    // 🆕 NUEVOS MÉTODOS DE CONTRIBUCIÓN
    public void addContributionPoints(int points) {
        this.contributionScore += points;
    }
    
    public void removeContributionPoints(int points) {
        this.contributionScore = Math.max(0, this.contributionScore - points);
    }
    
    public boolean isEligibleForPromotion() {
        // Lógica para determinar si es elegible para promoción
        return contributionScore >= 100 && userType == UserType.VISITOR;
    }
    
    public int getApprovedProposalsCount() {
        return (int) routeProposals.stream()
                .filter(RouteProposal::isApproved)
                .count();
    }
    
    public int getActiveRoutesCount() {
        return (int) createdRoutes.stream()
                .filter(Route::getIsActive)
                .count();
    }
    
    public double getAverageRatingGiven() {
        return routeRatings.stream()
                .mapToInt(RouteRating::getRating)
                .average()
                .orElse(0.0);
    }
    
    public boolean hasRouteRating(Route route) {
        return routeRatings.stream()
                .anyMatch(rating -> rating.getRoute().equals(route));
    }
    
    public RouteRating getRouteRating(Route route) {
        return routeRatings.stream()
                .filter(rating -> rating.getRoute().equals(route))
                .findFirst()
                .orElse(null);
    }
    
    // UserDetails implementation (sin cambios)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + userType.name())
        );
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    // Getters and Setters (incluyendo los nuevos campos)
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // 🆕 GETTER/SETTER PARA CONTRIBUTION SCORE
    public Integer getContributionScore() {
        return contributionScore;
    }
    
    public void setContributionScore(Integer contributionScore) {
        this.contributionScore = contributionScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Getters and Setters para relaciones existentes
    public Set<UserFavorite> getFavorites() {
        return favorites;
    }
    
    public void setFavorites(Set<UserFavorite> favorites) {
        this.favorites = favorites;
    }
    
    public Set<PlaceReport> getReports() {
        return reports;
    }
    
    public void setReports(Set<PlaceReport> reports) {
        this.reports = reports;
    }
    
    public Set<NavigationHistory> getNavigationHistory() {
        return navigationHistory;
    }
    
    public void setNavigationHistory(Set<NavigationHistory> navigationHistory) {
        this.navigationHistory = navigationHistory;
    }
    
    // 🆕 GETTERS/SETTERS PARA NUEVAS RELACIONES
    public Set<Route> getCreatedRoutes() {
        return createdRoutes;
    }
    
    public void setCreatedRoutes(Set<Route> createdRoutes) {
        this.createdRoutes = createdRoutes;
    }
    
    public Set<RouteRating> getRouteRatings() {
        return routeRatings;
    }
    
    public void setRouteRatings(Set<RouteRating> routeRatings) {
        this.routeRatings = routeRatings;
    }
    
    public Set<RouteProposal> getRouteProposals() {
        return routeProposals;
    }
    
    public void setRouteProposals(Set<RouteProposal> routeProposals) {
        this.routeProposals = routeProposals;
    }
    
    public Set<UserPromotion> getPromotions() {
        return promotions;
    }
    
    public void setPromotions(Set<UserPromotion> promotions) {
        this.promotions = promotions;
    }
    
    public Set<UserPromotion> getPromotionsGiven() {
        return promotionsGiven;
    }
    
    public void setPromotionsGiven(Set<UserPromotion> promotionsGiven) {
        this.promotionsGiven = promotionsGiven;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}