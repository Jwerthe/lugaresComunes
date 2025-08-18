// NavigationController.java
package com.example.demo.controller;

import com.example.demo.dto.navigation.*;
import com.example.demo.service.NavigationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/navigation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NavigationController {

    private static final Logger logger = LoggerFactory.getLogger(NavigationController.class);

    @Autowired
    private NavigationService navigationService;

    /**
     * POST /api/navigation/start - Registrar inicio de navegación
     */
    @PostMapping("/start")
    public ResponseEntity<?> startNavigation(@Valid @RequestBody NavigationStartRequest request) {
        try {
            NavigationHistoryDTO navigation = navigationService.startNavigation(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Navegación iniciada exitosamente");
            response.put("data", navigation);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error iniciando navegación: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * POST /api/navigation/complete - Registrar finalización de navegación
     */
    @PostMapping("/complete")
    public ResponseEntity<?> completeNavigation(@Valid @RequestBody NavigationCompleteRequest request) {
        try {
            NavigationHistoryDTO navigation = navigationService.completeNavigation(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Navegación completada exitosamente");
            response.put("data", navigation);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error completando navegación: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/navigation/history - Obtener historial de navegación del usuario
     */
    @GetMapping("/history")
    public ResponseEntity<?> getNavigationHistory() {
        try {
            List<NavigationHistoryDTO> history = navigationService.getNavigationHistory();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Historial de navegación obtenido exitosamente");
            response.put("count", history.size());
            response.put("data", history);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo historial de navegación: {}", e.getMessage());
            return createErrorResponse("Error obteniendo historial", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔧 MÉTODOS AUXILIARES

    private HttpStatus determineHttpStatus(Exception e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("not found") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("bad request") || message.contains("obligatorio")) {
            return HttpStatus.BAD_REQUEST;
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
        response.put("message", "Navigation service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}