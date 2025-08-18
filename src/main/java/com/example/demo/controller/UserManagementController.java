// UserManagementController.java
package com.example.demo.controller;

import com.example.demo.dto.user.*;
import com.example.demo.service.UserPromotionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserPromotionService promotionService;

    // 🛡️ ENDPOINTS SOLO ADMIN

    /**
     * PUT /api/users/{userId}/promote - Promover usuario a ADMIN
     */
    @PutMapping("/{userId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUser(@PathVariable UUID userId, 
                                        @Valid @RequestBody PromoteUserRequest request) {
        try {
            UserPromotionDTO promotion = promotionService.promoteUser(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario promovido exitosamente");
            response.put("data", promotion);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error promoviendo usuario {}: {}", userId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/users/contributors - Ver usuarios con más contribuciones
     */
    @GetMapping("/contributors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTopContributors() {
        try {
            List<UserDTO> contributors = promotionService.getTopContributors();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Top contributors obtenidos exitosamente");
            response.put("count", contributors.size());
            response.put("data", contributors);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo top contributors: {}", e.getMessage());
            return createErrorResponse("Error obteniendo contributors", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/users/promotions/recent - Ver promociones recientes
     */
    @GetMapping("/promotions/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecentPromotions() {
        try {
            List<UserPromotionDTO> promotions = promotionService.getRecentPromotions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Promociones recientes obtenidas exitosamente");
            response.put("count", promotions.size());
            response.put("data", promotions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo promociones recientes: {}", e.getMessage());
            return createErrorResponse("Error obteniendo promociones", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔧 MÉTODOS AUXILIARES

    private HttpStatus determineHttpStatus(Exception e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("not found") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("bad request") || message.contains("obligatorio")) {
            return HttpStatus.BAD_REQUEST;
        } else if (message.contains("forbidden") || message.contains("no permitido")) {
            return HttpStatus.FORBIDDEN;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ResponseEntity<?> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User management service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}