package com.tastytreat.backend.tasty_treat_express_backend.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tastytreat.backend.tasty_treat_express_backend.models.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByRestaurantRestaurantId(String restaurantId);

    List<Report> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Report> findByReportType(String reportType);

    List<Report> findByRestaurantRestaurantIdAndReportType(String restaurantId, String reportType);

    List<Report> findByUserId(long userId);

    List<Report> findByStartDateBetweenAndReportType(LocalDate startDate, LocalDate endDate, String reportType,
            Pageable pageable);

    long countByReportType(String reportType);
}

