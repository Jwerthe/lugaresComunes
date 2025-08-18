package com.example.demo.service;

import com.example.demo.dto.route.*;
import com.example.demo.entity.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.place.PlaceDTO;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AuthService authService;

    // 🌐 ENDPOINTS PÚBLICOS (sin autenticación)

    /**
     * GET /api/routes/destinations - Lista destinos disponibles con cantidad de rutas
     */
    public List<PlaceDTO> getAvailableDestinations() {
        logger.info("🎯 Obteniendo destinos disponibles con rutas");
        
        List<Place> destinations = routeRepository.findByIsActiveTrueOrderByAverageRatingDesc()
                .stream()
                .map(Route::getToPlace)
                .distinct()
                .collect(Collectors.toList());

        return destinations.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/routes/to/{placeId} - Rutas disponibles a un destino específico
     */
    public List<RouteDTO> getRoutesToDestination(UUID placeId) {
        logger.info("🗺️ Obteniendo rutas para destino: {}", placeId);
        
        Place destination = placeService.getPlaceEntityById(placeId);
        List<Route> routes = routeRepository.findByToPlaceIdAndIsActiveTrue(placeId);
        
        // Ordenar por calificación promedio y uso
        routes.sort((r1, r2) -> {
            int ratingCompare = r2.getAverageRating().compareTo(r1.getAverageRating());
            if (ratingCompare != 0) return ratingCompare;
            return r2.getTimesUsed().compareTo(r1.getTimesUsed());
        });

        return routes.stream()
                .map(RouteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/routes/{routeId}/points - Puntos detallados de una ruta
     */
    public List<RoutePointDTO> getRoutePoints(UUID routeId) {
        logger.info("📍 Obteniendo puntos de ruta: {}", routeId);
        
        Route route = getRouteEntityById(routeId);
        if (!route.getIsActive()) {
            throw new BadRequestException("La ruta no está activa");
        }

        List<RoutePoint> points = routePointRepository.findByRouteIdOrderByOrderIndexAsc(routeId);
        
        return points.stream()
                .map(RoutePointDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/routes/nearest?lat=X&lng=Y&destination=placeId - Ruta más cercana al usuario
     */
    public RouteDTO getNearestRoute(BigDecimal latitude, BigDecimal longitude, UUID destinationId) {
        logger.info("🎯 Buscando ruta más cercana desde {},{} a destino: {}", latitude, longitude, destinationId);
        
        validateCoordinates(latitude, longitude);
        Place destination = placeService.getPlaceEntityById(destinationId);
        
        // Buscar rutas cercanas en un radio de 5km
        List<Route> nearbyRoutes = routeRepository.findNearbyRoutes(latitude, longitude, new BigDecimal("5.0"))
                .stream()
                .filter(route -> route.getToPlace().getId().equals(destinationId))
                .filter(Route::getIsActive)
                .collect(Collectors.toList());

        if (nearbyRoutes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron rutas cercanas a este destino");
        }

        // Seleccionar la mejor ruta (más cercana + mejor calificada)
        Route bestRoute = nearbyRoutes.stream()
                .max((r1, r2) -> {
                    // Priorizar calificación alta
                    int ratingCompare = r1.getAverageRating().compareTo(r2.getAverageRating());
                    if (ratingCompare != 0) return ratingCompare;
                    // Luego por uso frecuente
                    return r1.getTimesUsed().compareTo(r2.getTimesUsed());
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo determinar la mejor ruta"));

        return RouteDTO.fromEntity(bestRoute);
    }

    /**
     * GET /api/routes/{routeId}/details - Información completa de una ruta
     */
    public RouteDTO getRouteDetails(UUID routeId) {
        logger.info("📋 Obteniendo detalles completos de ruta: {}", routeId);
        
        Route route = getRouteEntityById(routeId);
        return RouteDTO.fromEntityWithPoints(route);
    }

    // 🔐 ENDPOINTS PROTEGIDOS (requieren JWT - cualquier usuario logueado)

    /**
     * POST /api/routes/{routeId}/rating - Calificar una ruta (1-5 estrellas)
     */
    public RouteRatingDTO rateRoute(UUID routeId, CreateRouteRatingRequest request) {
        logger.info("⭐ Usuario calificando ruta {} con {} estrellas", routeId, request.getRating());
        
        User currentUser = authService.getCurrentUserEntity();
        Route route = getRouteEntityById(routeId);
        
        // Verificar que el usuario haya completado una navegación con esta ruta
        boolean hasCompletedNavigation = currentUser.getNavigationHistory().stream()
                .anyMatch(nav -> nav.getRouteUsed() != null && 
                               nav.getRouteUsed().getId().equals(routeId) && 
                               nav.getRouteCompleted());
        
        if (!hasCompletedNavigation) {
            throw new BadRequestException("Solo puedes calificar rutas que hayas completado");
        }

        // Verificar si ya calificó esta ruta
        if (currentUser.hasRouteRating(route)) {
            throw new BadRequestException("Ya has calificado esta ruta");
        }

        RouteRating rating = new RouteRating(route, currentUser, request.getRating(), request.getComment());
        
        // Agregar puntos de contribución
        currentUser.addContributionPoints(5); // 5 puntos por calificar una ruta
        
        route.addRating(rating);
        routeRepository.save(route);
        
        logger.info("✅ Ruta calificada exitosamente por usuario: {}", currentUser.getEmail());
        return RouteRatingDTO.fromEntity(rating);
    }

    // 🛡️ ENDPOINTS SOLO ADMIN

    /**
     * POST /api/routes - Crear ruta oficial directamente
     */
    public RouteDTO createRoute(CreateRouteRequest request) {
        logger.info("🚀 Admin creando nueva ruta: {}", request.getName());
        
        User admin = authService.getCurrentUserEntity();
        Place destination = placeService.getPlaceEntityById(request.getToPlaceId());
        
        validateCreateRouteRequest(request);

        Route route = new Route();
        mapRequestToEntity(request, route, destination, admin);

        Route savedRoute = routeRepository.save(route);
        
        // Crear puntos de la ruta
        createRoutePoints(savedRoute, request.getRoutePoints());
        
        // Actualizar contador de rutas del lugar de destino
        destination.incrementRouteCount();
        
        logger.info("✅ Ruta creada exitosamente: {} (ID: {})", savedRoute.getName(), savedRoute.getId());
        return RouteDTO.fromEntity(savedRoute);
    }

    /**
     * PUT /api/routes/{routeId} - Actualizar ruta existente
     */
    public RouteDTO updateRoute(UUID routeId, CreateRouteRequest request) {
        logger.info("✏️ Admin actualizando ruta: {}", routeId);
        
        Route route = getRouteEntityById(routeId);
        User admin = authService.getCurrentUserEntity();
        
        validateCreateRouteRequest(request);
        
        // Si cambió el destino, actualizar contadores
        if (!route.getToPlace().getId().equals(request.getToPlaceId())) {
            route.getToPlace().decrementRouteCount();
            Place newDestination = placeService.getPlaceEntityById(request.getToPlaceId());
            newDestination.incrementRouteCount();
            route.setToPlace(newDestination);
        }

        // Actualizar campos básicos
        route.setName(request.getName());
        route.setDescription(request.getDescription());
        route.setFromLatitude(request.getFromLatitude());
        route.setFromLongitude(request.getFromLongitude());
        route.setFromDescription(request.getFromDescription());
        route.setTotalDistance(request.getTotalDistance());
        route.setEstimatedTime(request.getEstimatedTime());
        route.setDifficulty(request.getDifficulty());
        route.setIsActive(request.getIsActive());

        // Recrear puntos de la ruta
        routePointRepository.deleteAll(route.getRoutePoints());
        route.getRoutePoints().clear();
        createRoutePoints(route, request.getRoutePoints());

        Route updatedRoute = routeRepository.save(route);
        
        logger.info("✅ Ruta actualizada exitosamente: {}", updatedRoute.getName());
        return RouteDTO.fromEntity(updatedRoute);
    }

    /**
     * DELETE /api/routes/{routeId} - Eliminar ruta
     */
    public void deleteRoute(UUID routeId) {
        logger.info("🗑️ Admin eliminando ruta: {}", routeId);
        
        Route route = getRouteEntityById(routeId);
        
        // Decrementar contador del lugar de destino
        route.getToPlace().decrementRouteCount();
        
        routeRepository.delete(route);
        
        logger.info("✅ Ruta eliminada exitosamente: {}", route.getName());
    }

    /**
     * GET /api/routes/analytics - Estadísticas de uso de rutas
     */
    public Map<String, Object> getRouteAnalytics() {
        logger.info("📊 Obteniendo analytics de rutas");
        
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalRoutes", routeRepository.countActiveRoutes());
        analytics.put("averageRating", routeRepository.getAverageRatingAllRoutes());
        analytics.put("totalDestinations", getAvailableDestinations().size());
        
        // Rutas más populares
        List<Route> topRoutes = routeRepository.findByIsActiveTrueOrderByTimesUsedDesc()
                .stream()
                .limit(5)
                .collect(Collectors.toList());
        analytics.put("topRoutes", topRoutes.stream().map(RouteDTO::fromEntity).collect(Collectors.toList()));
        
        // Rutas que necesitan revisión
        List<Route> problematicRoutes = routeRepository.findRoutesNeedingReview(3.0, 3);
        analytics.put("routesNeedingReview", problematicRoutes.stream().map(RouteDTO::fromEntity).collect(Collectors.toList()));
        
        return analytics;
    }

    // 🔧 MÉTODOS AUXILIARES PRIVADOS

    private Route getRouteEntityById(UUID routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta", "id", routeId));
    }

    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new BadRequestException("Latitud y longitud son obligatorias");
        }

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitud debe estar entre -90 y 90");
        }

        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitud debe estar entre -180 y 180");
        }
    }

    private void validateCreateRouteRequest(CreateRouteRequest request) {
        validateCoordinates(request.getFromLatitude(), request.getFromLongitude());
        
        if (request.getRoutePoints() == null || request.getRoutePoints().isEmpty()) {
            throw new BadRequestException("La ruta debe tener al menos un punto");
        }
        
        // Verificar que tenga punto de inicio y fin
        boolean hasStart = request.getRoutePoints().stream()
                .anyMatch(p -> p.getPointType() == RoutePointType.START);
        boolean hasEnd = request.getRoutePoints().stream()
                .anyMatch(p -> p.getPointType() == RoutePointType.END);
                
        if (!hasStart) {
            throw new BadRequestException("La ruta debe tener un punto de inicio");
        }
        if (!hasEnd) {
            throw new BadRequestException("La ruta debe tener un punto final");
        }
        
        // Verificar orden de índices
        List<Integer> indices = request.getRoutePoints().stream()
                .map(CreateRoutePointRequest::getOrderIndex)
                .sorted()
                .collect(Collectors.toList());
                
        for (int i = 0; i < indices.size(); i++) {
            if (!indices.get(i).equals(i)) {
                throw new BadRequestException("Los índices de orden deben ser consecutivos empezando desde 0");
            }
        }
    }

    private void mapRequestToEntity(CreateRouteRequest request, Route route, Place destination, User admin) {
        route.setName(request.getName().trim());
        route.setDescription(request.getDescription());
        route.setFromLatitude(request.getFromLatitude());
        route.setFromLongitude(request.getFromLongitude());
        route.setFromDescription(request.getFromDescription());
        route.setToPlace(destination);
        route.setTotalDistance(request.getTotalDistance());
        route.setEstimatedTime(request.getEstimatedTime());
        route.setDifficulty(request.getDifficulty());
        route.setIsActive(request.getIsActive());
        route.setCreatedBy(admin);
    }

    private void createRoutePoints(Route route, List<CreateRoutePointRequest> pointRequests) {
        List<RoutePoint> points = new ArrayList<>();
        
        for (CreateRoutePointRequest pointRequest : pointRequests) {
            RoutePoint point = new RoutePoint();
            point.setRoute(route);
            point.setLatitude(pointRequest.getLatitude());
            point.setLongitude(pointRequest.getLongitude());
            point.setOrderIndex(pointRequest.getOrderIndex());
            point.setPointType(pointRequest.getPointType());
            point.setInstruction(pointRequest.getInstruction());
            point.setLandmarkDescription(pointRequest.getLandmarkDescription());
            point.setDistanceFromPrevious(pointRequest.getDistanceFromPrevious());
            
            points.add(point);
        }
        
        routePointRepository.saveAll(points);
        route.setRoutePoints(points);
    }
}