// NavigationService.java
package com.example.demo.service;

import com.example.demo.dto.navigation.*;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NavigationService {

    private static final Logger logger = LoggerFactory.getLogger(NavigationService.class);

    @Autowired
    private NavigationHistoryRepository navigationHistoryRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AuthService authService;

    /**
     * POST /api/navigation/start - Registrar inicio de navegación
     */
    public NavigationHistoryDTO startNavigation(NavigationStartRequest request) {
        logger.info("🧭 Usuario iniciando navegación a lugar: {}", request.getToPlaceId());
        
        User currentUser = authService.getCurrentUserEntity();
        Place destination = placeService.getPlaceEntityById(request.getToPlaceId());
        
        validateCoordinates(request.getFromLatitude(), request.getFromLongitude());

        NavigationHistory navigation = new NavigationHistory();
        navigation.setUser(currentUser);
        navigation.setToPlace(destination);
        navigation.setFromLat(request.getFromLatitude());
        navigation.setFromLng(request.getFromLongitude());
        navigation.setNavigationStartedAt(java.time.LocalDateTime.now());
        
        // Si especificó una ruta, asociarla
        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ruta", "id", request.getRouteId()));
            
            if (!route.getIsActive()) {
                throw new BadRequestException("La ruta seleccionada no está activa");
            }
            
            if (!route.getToPlace().getId().equals(request.getToPlaceId())) {
                throw new BadRequestException("La ruta seleccionada no va al destino especificado");
            }
            
            navigation.setRouteUsed(route);
        }

        NavigationHistory savedNavigation = navigationHistoryRepository.save(navigation);
        
        logger.info("✅ Navegación iniciada para usuario: {}", currentUser.getEmail());
        return NavigationHistoryDTO.fromEntity(savedNavigation);
    }

    /**
     * POST /api/navigation/complete - Registrar finalización de navegación
     */
    public NavigationHistoryDTO completeNavigation(NavigationCompleteRequest request) {
        logger.info("✅ Usuario completando navegación: {}", request.getNavigationId());
        
        User currentUser = authService.getCurrentUserEntity();
        NavigationHistory navigation = navigationHistoryRepository.findById(request.getNavigationId())
                .orElseThrow(() -> new ResourceNotFoundException("Navegación", "id", request.getNavigationId()));
        
        // Verificar que sea la navegación del usuario actual
        if (!navigation.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("No puedes completar la navegación de otro usuario");
        }
        
        if (navigation.getNavigationCompletedAt() != null) {
            throw new BadRequestException("Esta navegación ya fue completada");
        }

        // Completar navegación
        if (request.getRouteCompleted() != null) {
            navigation.completeNavigationWithRoute(request.getRouteCompleted());
        } else {
            navigation.completeNavigation();
        }

        NavigationHistory completedNavigation = navigationHistoryRepository.save(navigation);
        
        // Agregar puntos de contribución por completar navegación
        currentUser.addContributionPoints(2); // 2 puntos por navegación completada
        
        logger.info("✅ Navegación completada exitosamente");
        return NavigationHistoryDTO.fromEntity(completedNavigation);
    }

    /**
     * GET /api/navigation/history - Obtener historial de navegación del usuario
     */
    public List<NavigationHistoryDTO> getNavigationHistory() {
        logger.info("📋 Usuario obteniendo historial de navegación");
        
        User currentUser = authService.getCurrentUserEntity();
        List<NavigationHistory> history = navigationHistoryRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        
        return history.stream()
                .map(NavigationHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 🔧 MÉTODOS AUXILIARES PRIVADOS

    private void validateCoordinates(java.math.BigDecimal latitude, java.math.BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new BadRequestException("Latitud y longitud son obligatorias");
        }

        if (latitude.compareTo(new java.math.BigDecimal("-90")) < 0 || 
            latitude.compareTo(new java.math.BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitud debe estar entre -90 y 90");
        }

        if (longitude.compareTo(new java.math.BigDecimal("-180")) < 0 || 
            longitude.compareTo(new java.math.BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitud debe estar entre -180 y 180");
        }
    }
}