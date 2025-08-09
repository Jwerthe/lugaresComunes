package com.example.demo.controller;


import com.example.demo.dto.place.CreatePlaceRequest;
import com.example.demo.dto.place.PlaceDTO;
import com.example.demo.dto.place.UpdatePlaceRequest;
import com.example.demo.service.PlaceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/places")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlaceController {

    private static final Logger logger = LoggerFactory.getLogger(PlaceController.class);

    @Autowired
    private PlaceService placeService;

    // Obtener todos los lugares (público)
    @GetMapping("")
    public ResponseEntity<?> getAllPlaces() {
        try {
            List<PlaceDTO> places = placeService.getAllPlaces();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugares obtenidos exitosamente");
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo todos los lugares: {}", e.getMessage());
            return createErrorResponse("Error obteniendo lugares", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener lugar por ID (público)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaceById(@PathVariable UUID id) {
        try {
            PlaceDTO place = placeService.getPlaceById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar obtenido exitosamente");
            response.put("data", place);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugar por ID {}: {}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Buscar lugares por texto (público)
    @GetMapping("/search")
    public ResponseEntity<?> searchPlaces(@RequestParam(required = false) String q) {
        try {
            List<PlaceDTO> places = placeService.searchPlaces(q);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda completada exitosamente");
            response.put("query", q);
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en búsqueda de lugares: {}", e.getMessage());
            return createErrorResponse("Error en búsqueda", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener lugares por tipo (público)
    @GetMapping("/type/{placeType}")
    public ResponseEntity<?> getPlacesByType(@PathVariable String placeType) {
        try {
            List<PlaceDTO> places = placeService.getPlacesByType(placeType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugares por tipo obtenidos exitosamente");
            response.put("placeType", placeType);
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugares por tipo {}: {}", placeType, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener lugares disponibles (público)
    @GetMapping("/available")
    public ResponseEntity<?> getAvailablePlaces(@RequestParam(defaultValue = "true") Boolean isAvailable) {
        try {
            List<PlaceDTO> places = placeService.getAvailablePlaces(isAvailable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugares por disponibilidad obtenidos exitosamente");
            response.put("isAvailable", isAvailable);
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugares disponibles: {}", e.getMessage());
            return createErrorResponse("Error obteniendo lugares disponibles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener lugares por edificio (público)
    @GetMapping("/building/{buildingName}")
    public ResponseEntity<?> getPlacesByBuilding(@PathVariable String buildingName) {
        try {
            List<PlaceDTO> places = placeService.getPlacesByBuilding(buildingName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugares por edificio obtenidos exitosamente");
            response.put("building", buildingName);
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugares por edificio {}: {}", buildingName, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Buscar lugar por código what3words (público)
    @GetMapping("/what3words")
    public ResponseEntity<?> getPlaceByWhat3words(@RequestParam String code) {
        try {
            PlaceDTO place = placeService.getPlaceByWhat3words(code);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar por what3words obtenido exitosamente");
            response.put("what3words", code);
            response.put("data", place);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugar por what3words {}: {}", code, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Obtener lugares cercanos (público)
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "1.0") BigDecimal radius) {
        try {
            List<PlaceDTO> places = placeService.getNearbyPlaces(lat, lng, radius);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugares cercanos obtenidos exitosamente");
            response.put("location", Map.of("latitude", lat, "longitude", lng));
            response.put("radius", radius + "km");
            response.put("count", places.size());
            response.put("data", places);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo lugares cercanos: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Crear nuevo lugar (solo ADMIN)
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        try {
            PlaceDTO place = placeService.createPlace(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar creado exitosamente");
            response.put("data", place);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creando lugar: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar lugar existente (solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePlace(@PathVariable UUID id, @Valid @RequestBody UpdatePlaceRequest request) {
        try {
            PlaceDTO place = placeService.updatePlace(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar actualizado exitosamente");
            response.put("data", place);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error actualizando lugar {}: {}", id, e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return createErrorResponse(e.getMessage(), status);
        }
    }

    // Eliminar lugar (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePlace(@PathVariable UUID id) {
        try {
            placeService.deletePlace(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lugar eliminado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error eliminando lugar {}: {}", id, e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return createErrorResponse(e.getMessage(), status);
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
        response.put("message", "Places service is healthy");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
