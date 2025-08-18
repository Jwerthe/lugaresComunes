// RouteProposalRepository.java
package com.example.demo.repository;

import com.example.demo.entity.RouteProposal;
import com.example.demo.entity.ProposalStatus;
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
public interface RouteProposalRepository extends JpaRepository<RouteProposal, UUID> {
    
    // Propuestas por usuario
    List<RouteProposal> findByProposedByOrderByCreatedAtDesc(User proposedBy);
    
    Page<RouteProposal> findByProposedBy(User proposedBy, Pageable pageable);
    
    // Propuestas por estado
    List<RouteProposal> findByStatusOrderByCreatedAtDesc(ProposalStatus status);
    
    List<RouteProposal> findByStatusOrderByCreatedAtAsc(ProposalStatus status);
    
    Page<RouteProposal> findByStatus(ProposalStatus status, Pageable pageable);
    
    // Propuestas pendientes
    //List<RouteProposal> findByStatusOrderByCreatedAtAsc(ProposalStatus status);
    
    // Propuestas por destino
    List<RouteProposal> findByToPlace(Place toPlace);
    
    List<RouteProposal> findByToPlaceAndStatus(Place toPlace, ProposalStatus status);
    
    // Propuestas revisadas por admin
    List<RouteProposal> findByReviewedBy(User reviewedBy);
    
    // Búsquedas con filtros
    @Query("SELECT rp FROM RouteProposal rp WHERE " +
           "(:status IS NULL OR rp.status = :status) AND " +
           "(:proposedBy IS NULL OR rp.proposedBy = :proposedBy) AND " +
           "(:toPlace IS NULL OR rp.toPlace = :toPlace)")
    List<RouteProposal> findProposalsWithFilters(@Param("status") ProposalStatus status,
                                                 @Param("proposedBy") User proposedBy,
                                                 @Param("toPlace") Place toPlace);
    
    // Estadísticas
    long countByStatus(ProposalStatus status);
    
    long countByProposedBy(User proposedBy);
    
    long countByProposedByAndStatus(User proposedBy, ProposalStatus status);
    
    @Query("SELECT COUNT(rp) FROM RouteProposal rp WHERE rp.createdAt >= :startDate")
    long countProposalsSince(@Param("startDate") LocalDateTime startDate);
    
    // Propuestas recientes
    @Query("SELECT rp FROM RouteProposal rp WHERE rp.status = 'PENDING' AND rp.createdAt >= :since ORDER BY rp.createdAt DESC")
    List<RouteProposal> findRecentPendingProposals(@Param("since") LocalDateTime since);
    
    // Top contributors
    @Query("SELECT rp.proposedBy, COUNT(rp) FROM RouteProposal rp WHERE rp.status = 'APPROVED' GROUP BY rp.proposedBy ORDER BY COUNT(rp) DESC")
    List<Object[]> findTopContributors();
}
