package com.example.demo.controller;


import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.service.UserFavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoritesController {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesController.class);

    @Autowired
    private UserFavoriteService userFavoriteService;

    // Obtener todos los favoritos del usuario actual
    @GetMapping("")
    public ResponseEntity<?> getUserFavorites() {
        try {
            List<PlaceDTO> favorites = userFavoriteService.getCurrentUserFavorites();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Favoritos obtenidos exitosamente");
            response.put("count", favorites.size());
            response.put("data", favorites);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo favoritos: {}", e.getMessage());
            return createErrorResponse("Error obteniendo favoritos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Agregar lugar a favoritos
    @PostMapping("/{placeId}")
    public ResponseEntity<?> addToFavorites(@PathVariable UUID placeId) {
        try {
            PlaceDTO place = userFavoriteService.addToFavorites(placeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar agregado a favoritos exitosamente");
            response.put("data", place);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error agregando lugar {} a favoritos: {}", placeId, e.getMessage());
            HttpStatus status = determineHttpStatus(e);
            return createErrorResponse(e.getMessage(), status);
        }
    }

    // Remover lugar de favoritos
    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable UUID placeId) {
        try {
            userFavoriteService.removeFromFavorites(placeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar removido de favoritos exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error removiendo lugar {} de favoritos: {}", placeId, e.getMessage());
            HttpStatus status = determineHttpStatus(e);
            return createErrorResponse(e.getMessage(), status);
        }
    }

    // Verificar si un lugar está en favoritos
    @GetMapping("/check/{placeId}")
    public ResponseEntity<?> checkIfFavorite(@PathVariable UUID placeId) {
        try {
            boolean isFavorite = userFavoriteService.isPlaceInFavorites(placeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Verificación completada exitosamente");
            response.put("placeId", placeId);
            response.put("isFavorite", isFavorite);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error verificando favorito para lugar {}: {}", placeId, e.getMessage());
            return createErrorResponse("Error verificando favorito", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Toggle favorito (agregar si no está, remover si está)
    @PutMapping("/toggle/{placeId}")
    public ResponseEntity<?> toggleFavorite(@PathVariable UUID placeId) {
        try {
            UserFavoriteService.FavoriteToggleResult result = userFavoriteService.toggleFavorite(placeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result.getMessage());
            response.put("isNowFavorite", result.isNowFavorite());
            response.put("data", result.getPlace());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en toggle favorito para lugar {}: {}", placeId, e.getMessage());
            HttpStatus status = determineHttpStatus(e);
            return createErrorResponse(e.getMessage(), status);
        }
    }

    // Obtener cantidad de favoritos
    @GetMapping("/count")
    public ResponseEntity<?> getFavoritesCount() {
        try {
            int count = userFavoriteService.getFavoritesCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Conteo de favoritos obtenido exitosamente");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo conteo de favoritos: {}", e.getMessage());
            return createErrorResponse("Error obteniendo conteo de favoritos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Limpiar todos los favoritos
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearAllFavorites() {
        try {
            userFavoriteService.clearAllFavorites();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todos los favoritos han sido eliminados exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error limpiando favoritos: {}", e.getMessage());
            return createErrorResponse("Error limpiando favoritos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método auxiliar para determinar el status HTTP basado en la excepción
    private HttpStatus determineHttpStatus(Exception e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("not found") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("conflict") || message.contains("ya está")) {
            return HttpStatus.CONFLICT;
        } else if (message.contains("bad request") || message.contains("obligatorio")) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    // Método auxiliar para crear respuestas de error
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
        response.put("message", "Favorites service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
