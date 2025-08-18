// RoutePointRepository.java
package com.example.demo.repository;

import com.example.demo.entity.RoutePoint;
import com.example.demo.entity.Route;
import com.example.demo.entity.RoutePointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, UUID> {
    
    List<RoutePoint> findByRouteOrderByOrderIndexAsc(Route route);
    
    List<RoutePoint> findByRouteIdOrderByOrderIndexAsc(UUID routeId);
    
    List<RoutePoint> findByRouteAndPointType(Route route, RoutePointType pointType);
    
    Optional<RoutePoint> findByRouteAndPointTypeAndOrderIndex(Route route, RoutePointType pointType, Integer orderIndex);
    
    // Encontrar punto de inicio y fin
    @Query("SELECT rp FROM RoutePoint rp WHERE rp.route = :route AND rp.pointType = 'START'")
    Optional<RoutePoint> findStartPoint(@Param("route") Route route);
    
    @Query("SELECT rp FROM RoutePoint rp WHERE rp.route = :route AND rp.pointType = 'END'")
    Optional<RoutePoint> findEndPoint(@Param("route") Route route);
    
    // Contar puntos por ruta
    long countByRoute(Route route);
    
    // Encontrar próximo punto en la secuencia
    @Query("SELECT rp FROM RoutePoint rp WHERE rp.route = :route AND rp.orderIndex > :currentIndex ORDER BY rp.orderIndex ASC")
    List<RoutePoint> findNextPoints(@Param("route") Route route, @Param("currentIndex") Integer currentIndex);
    
    // Encontrar punto anterior en la secuencia
    @Query("SELECT rp FROM RoutePoint rp WHERE rp.route = :route AND rp.orderIndex < :currentIndex ORDER BY rp.orderIndex DESC")
    List<RoutePoint> findPreviousPoints(@Param("route") Route route, @Param("currentIndex") Integer currentIndex);
}