package com.example.demo.repository;

import com.example.demo.entity.NavigationHistory;
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
    
    List<NavigationHistory> findByUserId(UUID userId);
    
    Page<NavigationHistory> findByUserId(UUID userId, Pageable pageable);
    
    List<NavigationHistory> findByToPlaceId(UUID placeId);
    
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.user.id = :userId " +
           "AND nh.createdAt BETWEEN :startDate AND :endDate")
    List<NavigationHistory> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT nh FROM NavigationHistory nh WHERE nh.user.id = :userId " +
           "ORDER BY nh.createdAt DESC")
    List<NavigationHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);
}