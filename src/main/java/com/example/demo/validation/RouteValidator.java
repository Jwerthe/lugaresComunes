// RouteValidator.java
package com.example.demo.validation;

import com.example.demo.dto.route.CreateRouteRequest;
import com.example.demo.dto.route.CreateRoutePointRequest;
import com.example.demo.entity.RoutePointType;
import com.example.demo.exception.RouteValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RouteValidator {
    
    private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");
    
    private static final int MAX_ROUTE_POINTS = 50;
    private static final int MIN_ROUTE_POINTS = 2;
    private static final int MAX_DISTANCE_METERS = 10000; // 10km
    private static final int MAX_TIME_MINUTES = 120; // 2 horas
    
    public void validateCreateRouteRequest(CreateRouteRequest request) {
        validateBasicRouteData(request);
        validateRoutePoints(request.getRoutePoints());
        validateRouteLogic(request);
    }
    
    private void validateBasicRouteData(CreateRouteRequest request) {
        // Validar nombre
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RouteValidationException("name", "El nombre es obligatorio");
        }
        
        if (request.getName().length() > 100) {
            throw new RouteValidationException("name", "El nombre no puede exceder 100 caracteres");
        }
        
        // Validar coordenadas de inicio
        validateCoordinates("fromLatitude", request.getFromLatitude(), 
                          "fromLongitude", request.getFromLongitude());
        
        // Validar distancia y tiempo
        if (request.getTotalDistance() != null) {
            if (request.getTotalDistance() <= 0) {
                throw new RouteValidationException("totalDistance", "La distancia debe ser mayor a 0");
            }
            if (request.getTotalDistance() > MAX_DISTANCE_METERS) {
                throw new RouteValidationException("totalDistance", 
                    "La distancia no puede exceder " + MAX_DISTANCE_METERS + " metros");
            }
        }
        
        if (request.getEstimatedTime() != null) {
            if (request.getEstimatedTime() <= 0) {
                throw new RouteValidationException("estimatedTime", "El tiempo estimado debe ser mayor a 0");
            }
            if (request.getEstimatedTime() > MAX_TIME_MINUTES) {
                throw new RouteValidationException("estimatedTime", 
                    "El tiempo estimado no puede exceder " + MAX_TIME_MINUTES + " minutos");
            }
        }
    }
    
    private void validateRoutePoints(List<CreateRoutePointRequest> points) {
        if (points == null || points.isEmpty()) {
            throw new RouteValidationException("routePoints", "La ruta debe tener al menos " + MIN_ROUTE_POINTS + " puntos");
        }
        
        if (points.size() < MIN_ROUTE_POINTS) {
            throw new RouteValidationException("routePoints", 
                "La ruta debe tener al menos " + MIN_ROUTE_POINTS + " puntos (inicio y fin)");
        }
        
        if (points.size() > MAX_ROUTE_POINTS) {
            throw new RouteValidationException("routePoints", 
                "La ruta no puede tener más de " + MAX_ROUTE_POINTS + " puntos");
        }
        
        validatePointSequence(points);
        validatePointTypes(points);
        validatePointCoordinates(points);
    }
    
    private void validatePointSequence(List<CreateRoutePointRequest> points) {
        // Verificar que los índices sean consecutivos
        List<Integer> indices = points.stream()
                .map(CreateRoutePointRequest::getOrderIndex)
                .sorted()
                .collect(Collectors.toList());
        
        for (int i = 0; i < indices.size(); i++) {
            if (!indices.get(i).equals(i)) {
                throw new RouteValidationException("routePoints", 
                    "Los índices de orden deben ser consecutivos empezando desde 0");
            }
        }
        
        // Verificar que no haya índices duplicados
        Map<Integer, Long> indexCounts = points.stream()
                .collect(Collectors.groupingBy(CreateRoutePointRequest::getOrderIndex, Collectors.counting()));
        
        indexCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    throw new RouteValidationException("routePoints", 
                        "Índice duplicado encontrado: " + entry.getKey());
                });
    }
    
    private void validatePointTypes(List<CreateRoutePointRequest> points) {
        Map<RoutePointType, Long> typeCounts = points.stream()
                .collect(Collectors.groupingBy(CreateRoutePointRequest::getPointType, Collectors.counting()));
        
        // Debe tener exactamente un punto de inicio
        Long startCount = typeCounts.getOrDefault(RoutePointType.START, 0L);
        if (startCount == 0) {
            throw new RouteValidationException("routePoints", "La ruta debe tener un punto de inicio (START)");
        }
        if (startCount > 1) {
            throw new RouteValidationException("routePoints", "La ruta solo puede tener un punto de inicio");
        }
        
        // Debe tener exactamente un punto final
        Long endCount = typeCounts.getOrDefault(RoutePointType.END, 0L);
        if (endCount == 0) {
            throw new RouteValidationException("routePoints", "La ruta debe tener un punto final (END)");
        }
        if (endCount > 1) {
            throw new RouteValidationException("routePoints", "La ruta solo puede tener un punto final");
        }
        
        // El primer punto debe ser START
        if (points.get(0).getPointType() != RoutePointType.START) {
            throw new RouteValidationException("routePoints", "El primer punto debe ser de tipo START");
        }
        
        // El último punto debe ser END
        if (points.get(points.size() - 1).getPointType() != RoutePointType.END) {
            throw new RouteValidationException("routePoints", "El último punto debe ser de tipo END");
        }
    }
    
    private void validatePointCoordinates(List<CreateRoutePointRequest> points) {
        for (int i = 0; i < points.size(); i++) {
            CreateRoutePointRequest point = points.get(i);
            
            try {
                validateCoordinates("latitude", point.getLatitude(), "longitude", point.getLongitude());
            } catch (RouteValidationException e) {
                throw new RouteValidationException("routePoints[" + i + "]", e.getMessage());
            }
            
            // Validar distancia desde punto anterior
            if (point.getDistanceFromPrevious() != null && point.getDistanceFromPrevious() < 0) {
                throw new RouteValidationException("routePoints[" + i + "].distanceFromPrevious", 
                    "La distancia no puede ser negativa");
            }
        }
    }
    
    private void validateRouteLogic(CreateRouteRequest request) {
        // Verificar que la distancia total sea consistente con los puntos
        Integer totalDistance = request.getTotalDistance();
        if (totalDistance != null) {
            int calculatedDistance = request.getRoutePoints().stream()
                    .filter(p -> p.getDistanceFromPrevious() != null)
                    .mapToInt(CreateRoutePointRequest::getDistanceFromPrevious)
                    .sum();
            
            // Permitir una diferencia de hasta 10%
            double tolerance = totalDistance * 0.1;
            if (Math.abs(totalDistance - calculatedDistance) > tolerance) {
                throw new RouteValidationException("totalDistance", 
                    "La distancia total no coincide con la suma de distancias entre puntos");
            }
        }
        
        // Verificar tiempo vs distancia (velocidad razonable de caminata: 3-6 km/h)
        if (request.getTotalDistance() != null && request.getEstimatedTime() != null) {
            double distanceKm = request.getTotalDistance() / 1000.0;
            double timeHours = request.getEstimatedTime() / 60.0;
            double speed = distanceKm / timeHours;
            
            if (speed < 1.0 || speed > 8.0) { // Entre 1 y 8 km/h es razonable
                throw new RouteValidationException("estimatedTime", 
                    "El tiempo estimado no es coherente con la distancia (velocidad: " + 
                    String.format("%.1f", speed) + " km/h)");
            }
        }
    }
    
    private void validateCoordinates(String latField, BigDecimal latitude, 
                                   String lngField, BigDecimal longitude) {
        if (latitude == null) {
            throw new RouteValidationException(latField, "La latitud es obligatoria");
        }
        
        if (longitude == null) {
            throw new RouteValidationException(lngField, "La longitud es obligatoria");
        }
        
        if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
            throw new RouteValidationException(latField, 
                "La latitud debe estar entre " + MIN_LATITUDE + " y " + MAX_LATITUDE);
        }
        
        if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
            throw new RouteValidationException(lngField, 
                "La longitud debe estar entre " + MIN_LONGITUDE + " y " + MAX_LONGITUDE);
        }
    }
    
    public void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        validateCoordinates("latitude", latitude, "longitude", longitude);
    }
}