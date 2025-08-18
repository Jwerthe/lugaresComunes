// RouteRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Route;
import com.example.demo.entity.RouteDifficulty;
import com.example.demo.entity.Place;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {
    
    // Rutas por destino
    List<Route> findByToPlaceAndIsActiveTrue(Place toPlace);
    
    List<Route> findByToPlaceId(UUID placeId);
    
    List<Route> findByToPlaceIdAndIsActiveTrue(UUID placeId);
    
    // Rutas por creador
    List<Route> findByCreatedBy(User createdBy);
    
    List<Route> findByCreatedByAndIsActiveTrue(User createdBy);
    
    // Rutas por dificultad
    List<Route> findByDifficultyAndIsActiveTrue(RouteDifficulty difficulty);
    
    // Rutas activas
    List<Route> findByIsActiveTrueOrderByAverageRatingDesc();
    
    List<Route> findByIsActiveTrueOrderByTimesUsedDesc();
    
    // Búsqueda de rutas
    @Query("SELECT r FROM Route r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.fromDescription) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Route> searchActiveRoutes(@Param("query") String query);
    
    // Rutas cercanas usando fórmula de Haversine
    @Query(value = "SELECT *, " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(from_latitude)) * " +
           "cos(radians(from_longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(from_latitude)))) AS distance " +
           "FROM routes " +
           "WHERE is_active = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(from_latitude)) * " +
           "cos(radians(from_longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(from_latitude)))) <= :radiusKm " +
           "ORDER BY distance", 
           nativeQuery = true)
    List<Route> findNearbyRoutes(@Param("lat") BigDecimal latitude, 
                                @Param("lng") BigDecimal longitude, 
                                @Param("radiusKm") BigDecimal radiusKm);
    
    // Rutas más populares
    @Query("SELECT r FROM Route r WHERE r.isActive = true AND r.averageRating >= :minRating " +
           "ORDER BY r.averageRating DESC, r.timesUsed DESC")
    List<Route> findTopRatedRoutes(@Param("minRating") Double minRating);
    
    // Estadísticas
    @Query("SELECT COUNT(r) FROM Route r WHERE r.isActive = true")
    long countActiveRoutes();
    
    @Query("SELECT COUNT(r) FROM Route r WHERE r.createdBy = :user")
    long countRoutesByUser(@Param("user") User user);
    
    @Query("SELECT AVG(r.averageRating) FROM Route r WHERE r.isActive = true AND r.totalRatings > 0")
    Double getAverageRatingAllRoutes();
    
    // Rutas que necesitan revisión (rating bajo)
    @Query("SELECT r FROM Route r WHERE r.isActive = true AND r.averageRating < :threshold AND r.totalRatings >= :minRatings")
    List<Route> findRoutesNeedingReview(@Param("threshold") Double threshold, @Param("minRatings") Integer minRatings);
}