package com.example.demo.controller;

import com.example.demo.dto.route.*;
import com.example.demo.service.RouteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.place.PlaceDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/routes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private RouteService routeService;

    // 🌐 ENDPOINTS PÚBLICOS (sin autenticación)

    /**
     * GET /api/routes/destinations - Lista destinos disponibles con cantidad de rutas
     */
    @GetMapping("/destinations")
    public ResponseEntity<?> getAvailableDestinations() {
        try {
            List<PlaceDTO> destinations = routeService.getAvailableDestinations();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Destinos disponibles obtenidos exitosamente");
            response.put("count", destinations.size());
            response.put("data", destinations);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo destinos disponibles: {}", e.getMessage());
            return createErrorResponse("Error obteniendo destinos disponibles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/routes/to/{placeId} - Rutas disponibles a un destino específico
     */
    @GetMapping("/to/{placeId}")
    public ResponseEntity<?> getRoutesToDestination(@PathVariable UUID placeId) {
        try {
            List<RouteDTO> routes = routeService.getRoutesToDestination(placeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rutas al destino obtenidas exitosamente");
            response.put("destinationId", placeId);
            response.put("count", routes.size());
            response.put("data", routes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo rutas para destino {}: {}", placeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/{routeId}/points - Puntos detallados de una ruta
     */
    @GetMapping("/{routeId}/points")
    public ResponseEntity<?> getRoutePoints(@PathVariable UUID routeId) {
        try {
            List<RoutePointDTO> points = routeService.getRoutePoints(routeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Puntos de ruta obtenidos exitosamente");
            response.put("routeId", routeId);
            response.put("count", points.size());
            response.put("data", points);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo puntos de ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/nearest?lat=X&lng=Y&destination=placeId - Ruta más cercana al usuario
     */
    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestRoute(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam UUID destination) {
        try {
            RouteDTO route = routeService.getNearestRoute(lat, lng, destination);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ruta más cercana encontrada exitosamente");
            response.put("userLocation", Map.of("latitude", lat, "longitude", lng));
            response.put("destinationId", destination);
            response.put("data", route);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error buscando ruta más cercana: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/{routeId}/details - Información completa de una ruta
     */
    @GetMapping("/{routeId}/details")
    public ResponseEntity<?> getRouteDetails(@PathVariable UUID routeId) {
        try {
            RouteDTO route = routeService.getRouteDetails(routeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Detalles de ruta obtenidos exitosamente");
            response.put("data", route);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo detalles de ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    // 🔐 ENDPOINTS PROTEGIDOS (requieren JWT - cualquier usuario logueado)

    /**
     * POST /api/routes/{routeId}/rating - Calificar una ruta (1-5 estrellas)
     */
    @PostMapping("/{routeId}/rating")
    public ResponseEntity<?> rateRoute(@PathVariable UUID routeId, 
                                      @Valid @RequestBody CreateRouteRatingRequest request) {
        try {
            RouteRatingDTO rating = routeService.rateRoute(routeId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ruta calificada exitosamente");
            response.put("routeId", routeId);
            response.put("data", rating);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error calificando ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/{routeId}/my-rating - Ver mi calificación de una ruta
     */
    @GetMapping("/{routeId}/my-rating")
    public ResponseEntity<?> getMyRating(@PathVariable UUID routeId) {
        try {
            // TODO: Implementar en RouteService
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Funcionalidad en desarrollo");
            response.put("routeId", routeId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo mi calificación para ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🛡️ ENDPOINTS SOLO ADMIN

    /**
     * POST /api/routes - Crear ruta oficial directamente
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        try {
            RouteDTO route = routeService.createRoute(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ruta creada exitosamente");
            response.put("data", route);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creando ruta: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * PUT /api/routes/{routeId} - Actualizar ruta existente
     */
    @PutMapping("/{routeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoute(@PathVariable UUID routeId, 
                                        @Valid @RequestBody CreateRouteRequest request) {
        try {
            RouteDTO route = routeService.updateRoute(routeId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ruta actualizada exitosamente");
            response.put("data", route);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error actualizando ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * DELETE /api/routes/{routeId} - Eliminar ruta
     */
    @DeleteMapping("/{routeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID routeId) {
        try {
            routeService.deleteRoute(routeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ruta eliminada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error eliminando ruta {}: {}", routeId, e.getMessage());
            return createErrorResponse(e.getMessage(), determineHttpStatus(e));
        }
    }

    /**
     * GET /api/routes/analytics - Estadísticas de uso de rutas
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRouteAnalytics() {
        try {
            Map<String, Object> analytics = routeService.getRouteAnalytics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Analytics obtenidos exitosamente");
            response.put("data", analytics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo analytics de rutas: {}", e.getMessage());
            return createErrorResponse("Error obteniendo analytics", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔧 MÉTODOS AUXILIARES

    private HttpStatus determineHttpStatus(Exception e) {
        String message = e.getMessage().toLowerCase();
        
        if (message.contains("not found") || message.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("bad request") || message.contains("obligatorio")) {
            return HttpStatus.BAD_REQUEST;
        } else if (message.contains("unauthorized") || message.contains("no autorizado")) {
            return HttpStatus.UNAUTHORIZED;
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
        response.put("message", "Routes service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}