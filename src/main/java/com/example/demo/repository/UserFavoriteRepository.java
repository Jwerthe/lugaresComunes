package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserFavorite;
import com.example.demo.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UUID> {
    
    List<UserFavorite> findByUser(User user);
    
    List<UserFavorite> findByUserId(UUID userId);
    
    Optional<UserFavorite> findByUserAndPlace(User user, Place place);
    
    boolean existsByUserAndPlace(User user, Place place);
    
    void deleteByUserAndPlace(User user, Place place);
    
    @Query("SELECT uf FROM UserFavorite uf JOIN FETCH uf.place WHERE uf.user.id = :userId")
    List<UserFavorite> findByUserIdWithPlace(@Param("userId") UUID userId);
}