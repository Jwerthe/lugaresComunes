package com.example.demo.repository;

import com.example.demo.entity.Place;
import com.example.demo.entity.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID> {
    
    List<Place> findByPlaceType(PlaceType placeType);
    
    List<Place> findByIsAvailable(Boolean isAvailable);
    
    List<Place> findByBuildingName(String buildingName);
    
    Optional<Place> findByWhat3words(String what3words);
    
    @Query("SELECT p FROM Place p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.what3words) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Place> searchPlaces(@Param("query") String query);
    
    @Query("SELECT p FROM Place p WHERE " +
           "p.placeType = :placeType AND p.isAvailable = :isAvailable")
    List<Place> findByPlaceTypeAndAvailability(
        @Param("placeType") PlaceType placeType, 
        @Param("isAvailable") Boolean isAvailable
    );
    
    // Custom query for nearby places using Haversine formula
    @Query(value = "SELECT *, " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(latitude)))) AS distance " +
           "FROM places " +
           "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(latitude)))) <= :radiusKm " +
           "ORDER BY distance", 
           nativeQuery = true)
    List<Place> findNearbyPlaces(@Param("lat") BigDecimal latitude, 
                                @Param("lng") BigDecimal longitude, 
                                @Param("radiusKm") BigDecimal radiusKm);
    
    @Query("SELECT p FROM Place p WHERE p.floorNumber = :floorNumber")
    List<Place> findByFloorNumber(@Param("floorNumber") Integer floorNumber);
}