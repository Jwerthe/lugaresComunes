// NavigationHistoryRepository.java
package com.example.demo.repository;

import com.example.demo.entity.NavigationHistory;
import com.example.demo.entity.Route;
import com.example.demo.entity.User;
import com.example.demo.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NavigationHistoryRepository extends JpaRepository<NavigationHistory, UUID> {
    
    // Navegaciones por usuario
    List<NavigationHistory> findByUserOrderByCreatedAtDesc(User user);
    
    List<NavigationHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Page<NavigationHistory> findByUser(User user, Pageable pageable);
    
    // Navegaciones por lugar de destino
    List<NavigationHistory> findByToPlace(Place toPlace);
    
    List<NavigationHistory> findByToPlaceOrderByCreatedAtDesc(Place toPlace);
    
    // Navegaciones por ruta utilizada
    List<NavigationHistory> findByRouteUsed(Route route);
    
    List<NavigationHistory> findByRouteUsedOrderByCreatedAtDesc(Route route);
    
    // Navegaciones completadas
    List<NavigationHistory> findByUserAndNavigationCompletedAtIsNotNull(User user);
    
    List<NavigationHistory> findByNavigationCompletedAtIsNotNull();
    
    // Navegaciones que usaron rutas
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.routeUsed IS NOT NULL")
    List<NavigationHistory> findNavigationsWithRoutes();
    
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.user = :user AND nh.routeUsed IS NOT NULL")
    List<NavigationHistory> findNavigationsWithRoutesByUser(@Param("user") User user);
    
    // Navegaciones completadas con rutas
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.routeUsed IS NOT NULL AND nh.routeCompleted = true")
    List<NavigationHistory> findCompletedNavigationsWithRoutes();
    
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.user = :user AND nh.routeUsed IS NOT NULL AND nh.routeCompleted = true")
    List<NavigationHistory> findCompletedNavigationsWithRoutesByUser(@Param("user") User user);
    
    // Verificar si un usuario completó una navegación con una ruta específica
    @Query("SELECT COUNT(nh) > 0 FROM NavigationHistory nh WHERE nh.user = :user AND nh.routeUsed = :route AND nh.routeCompleted = true")
    boolean hasUserCompletedRoute(@Param("user") User user, @Param("route") Route route);
    
    // Estadísticas
    @Query("SELECT COUNT(nh) FROM NavigationHistory nh WHERE nh.navigationCompletedAt IS NOT NULL")
    long countCompletedNavigations();
    
    @Query("SELECT COUNT(nh) FROM NavigationHistory nh WHERE nh.routeUsed IS NOT NULL")
    long countNavigationsWithRoutes();
    
    @Query("SELECT COUNT(nh) FROM NavigationHistory nh WHERE nh.routeCompleted = true")
    long countCompletedRouteNavigations();
    
    @Query("SELECT AVG(nh.durationSeconds) FROM NavigationHistory nh WHERE nh.durationSeconds IS NOT NULL")
    Double getAverageNavigationDuration();
    
    // Navegaciones recientes
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.createdAt >= :since ORDER BY nh.createdAt DESC")
    List<NavigationHistory> findRecentNavigations(@Param("since") LocalDateTime since);
    
    // Top destinos más navegados
    @Query("SELECT nh.toPlace, COUNT(nh) FROM NavigationHistory nh GROUP BY nh.toPlace ORDER BY COUNT(nh) DESC")
    List<Object[]> findTopNavigatedDestinations();
    
    // Rutas más utilizadas
    @Query("SELECT nh.routeUsed, COUNT(nh) FROM NavigationHistory nh WHERE nh.routeUsed IS NOT NULL GROUP BY nh.routeUsed ORDER BY COUNT(nh) DESC")
    List<Object[]> findMostUsedRoutes();
}