// RouteRatingRepository.java
package com.example.demo.repository;

import com.example.demo.entity.RouteRating;
import com.example.demo.entity.Route;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRatingRepository extends JpaRepository<RouteRating, UUID> {
    
    List<RouteRating> findByRoute(Route route);
    
    List<RouteRating> findByRouteOrderByCreatedAtDesc(Route route);
    
    List<RouteRating> findByUser(User user);
    
    Optional<RouteRating> findByRouteAndUser(Route route, User user);
    
    boolean existsByRouteAndUser(Route route, User user);
    
    // Calificaciones por rating
    List<RouteRating> findByRouteAndRating(Route route, Integer rating);
    
    List<RouteRating> findByRouteAndRatingGreaterThanEqual(Route route, Integer rating);
    
    List<RouteRating> findByRouteAndRatingLessThanEqual(Route route, Integer rating);
    
    // Estadísticas
    @Query("SELECT AVG(rr.rating) FROM RouteRating rr WHERE rr.route = :route")
    Double getAverageRatingForRoute(@Param("route") Route route);
    
    @Query("SELECT COUNT(rr) FROM RouteRating rr WHERE rr.route = :route")
    long countRatingsForRoute(@Param("route") Route route);
    
    @Query("SELECT COUNT(rr) FROM RouteRating rr WHERE rr.route = :route AND rr.rating = :rating")
    long countRatingsByValue(@Param("route") Route route, @Param("rating") Integer rating);
    
    // Distribución de calificaciones
    @Query("SELECT rr.rating, COUNT(rr) FROM RouteRating rr WHERE rr.route = :route GROUP BY rr.rating ORDER BY rr.rating")
    List<Object[]> getRatingDistribution(@Param("route") Route route);
    
    // Calificaciones con comentarios
    @Query("SELECT rr FROM RouteRating rr WHERE rr.route = :route AND rr.comment IS NOT NULL AND LENGTH(rr.comment) > 0 ORDER BY rr.createdAt DESC")
    List<RouteRating> findRatingsWithComments(@Param("route") Route route);
    
    // Calificaciones recientes
    @Query("SELECT rr FROM RouteRating rr WHERE rr.route = :route ORDER BY rr.createdAt DESC")
    List<RouteRating> findRecentRatings(@Param("route") Route route);
}