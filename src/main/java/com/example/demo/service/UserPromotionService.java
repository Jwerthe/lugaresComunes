// UserPromotionService.java - CORREGIDO
package com.example.demo.service;

import com.example.demo.dto.user.*;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserPromotionService {

    private static final Logger logger = LoggerFactory.getLogger(UserPromotionService.class);

    @Autowired
    private UserPromotionRepository promotionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    /**
     * PUT /api/users/{userId}/promote - Promover usuario a ADMIN
     */
    public UserPromotionDTO promoteUser(UUID userId, PromoteUserRequest request) {
        logger.info("🎉 Admin promoviendo usuario: {}", userId);
        
        User admin = authService.getCurrentUserEntity();
        User userToPromote = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));
        
        validatePromotionRequest(userToPromote, request, admin);

        UserType previousType = userToPromote.getUserType();
        userToPromote.setUserType(request.getToUserType());
        
        UserPromotion promotion = new UserPromotion(
            userToPromote, 
            previousType, 
            request.getToUserType(), 
            admin, 
            request.getReason()
        );

        UserPromotion savedPromotion = promotionRepository.save(promotion);
        userRepository.save(userToPromote);
        
        logger.info("✅ Usuario {} promovido de {} a {} por {}", 
                   userToPromote.getEmail(), 
                   previousType.getDisplayName(), 
                   request.getToUserType().getDisplayName(),
                   admin.getEmail());
        
        return UserPromotionDTO.fromEntity(savedPromotion);
    }

    /**
     * GET /api/users/contributors - Ver usuarios con más contribuciones
     */
    public List<UserDTO> getTopContributors() {
        logger.info("🏆 Obteniendo usuarios con más contribuciones");
        
        // 🔧 FIX: Manejar contributionScore null
        List<User> topUsers = userRepository.findAll().stream()
                .filter(user -> {
                    Integer score = user.getContributionScore();
                    return score != null && score > 0;
                })
                .sorted((u1, u2) -> {
                    Integer score1 = u1.getContributionScore() != null ? u1.getContributionScore() : 0;
                    Integer score2 = u2.getContributionScore() != null ? u2.getContributionScore() : 0;
                    return score2.compareTo(score1);
                })
                .limit(10)
                .collect(Collectors.toList());
        
        logger.info("📊 Encontrados {} usuarios con contribuciones", topUsers.size());
        
        return topUsers.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/users/promotions/recent - Ver promociones recientes
     */
    public List<UserPromotionDTO> getRecentPromotions() {
        logger.info("📋 Obteniendo promociones recientes");
        
        LocalDateTime since = LocalDateTime.now().minusDays(30); // Últimos 30 días
        List<UserPromotion> promotions = promotionRepository.findByPromotedAtAfterOrderByPromotedAtDesc(since);
        
        return promotions.stream()
                .map(UserPromotionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 🔧 MÉTODOS AUXILIARES PRIVADOS

    private void validatePromotionRequest(User userToPromote, PromoteUserRequest request, User admin) {
        // Verificar que no sea auto-promoción
        if (userToPromote.getId().equals(admin.getId())) {
            throw new BadRequestException("No puedes promocionarte a ti mismo");
        }
        
        // Verificar que el usuario actual sea ADMIN
        if (admin.getUserType() != UserType.ADMIN) {
            throw new BadRequestException("Solo los administradores pueden promover usuarios");
        }
        
        // Verificar que no sea una promoción redundante
        if (userToPromote.getUserType() == request.getToUserType()) {
            throw new BadRequestException("El usuario ya tiene ese tipo de usuario");
        }
        
        // Verificar que no haya sido promovido recientemente (últimos 7 días)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentPromotions = promotionRepository.countRecentPromotions(userToPromote, weekAgo);
        if (recentPromotions > 0) {
            throw new BadRequestException("El usuario fue promovido recientemente. Espera al menos 7 días.");
        }
        
        // Solo permitir promoción a ADMIN por ahora
        if (request.getToUserType() != UserType.ADMIN) {
            throw new BadRequestException("Solo se permite promoción a ADMIN");
        }
    }
}