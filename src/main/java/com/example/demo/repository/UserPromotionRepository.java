// UserPromotionRepository.java
package com.example.demo.repository;

import com.example.demo.entity.UserPromotion;
import com.example.demo.entity.User;
import com.example.demo.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPromotionRepository extends JpaRepository<UserPromotion, UUID> {
    
    List<UserPromotion> findByUserOrderByPromotedAtDesc(User user);
    
    List<UserPromotion> findByPromotedByOrderByPromotedAtDesc(User promotedBy);
    
    List<UserPromotion> findByToUserType(UserType toUserType);
    
    List<UserPromotion> findByFromUserTypeAndToUserType(UserType fromUserType, UserType toUserType);
    
    // Última promoción de un usuario
    Optional<UserPromotion> findTopByUserOrderByPromotedAtDesc(User user);
    
    // Promociones recientes
    List<UserPromotion> findByPromotedAtAfterOrderByPromotedAtDesc(LocalDateTime since);
    
    Page<UserPromotion> findAllByOrderByPromotedAtDesc(Pageable pageable);
    
    // Estadísticas
    long countByToUserType(UserType toUserType);
    
    long countByPromotedBy(User promotedBy);
    
    @Query("SELECT COUNT(up) FROM UserPromotion up WHERE up.promotedAt >= :startDate")
    long countPromotionsSince(@Param("startDate") LocalDateTime startDate);
    
    // Verificar si un usuario ha sido promovido recientemente
    @Query("SELECT COUNT(up) FROM UserPromotion up WHERE up.user = :user AND up.promotedAt >= :since")
    long countRecentPromotions(@Param("user") User user, @Param("since") LocalDateTime since);
    
    // Administradores más activos promoviendo
    @Query("SELECT up.promotedBy, COUNT(up) FROM UserPromotion up GROUP BY up.promotedBy ORDER BY COUNT(up) DESC")
    List<Object[]> findMostActivePromotingAdmins();
}