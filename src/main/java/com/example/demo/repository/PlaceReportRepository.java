package com.example.demo.repository;

import com.example.demo.entity.PlaceReport;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceReportRepository extends JpaRepository<PlaceReport, UUID> {
    
    List<PlaceReport> findByPlaceId(UUID placeId);
    
    List<PlaceReport> findByUserId(UUID userId);
    
    List<PlaceReport> findByStatus(ReportStatus status);
    
    List<PlaceReport> findByReportType(ReportType reportType);
    
    Page<PlaceReport> findByPlaceId(UUID placeId, Pageable pageable);
    
    @Query("SELECT pr FROM PlaceReport pr WHERE pr.place.id = :placeId AND pr.status = :status")
    List<PlaceReport> findByPlaceIdAndStatus(@Param("placeId") UUID placeId, 
                                            @Param("status") ReportStatus status);
}
