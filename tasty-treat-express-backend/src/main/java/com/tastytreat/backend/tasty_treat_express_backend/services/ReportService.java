package com.tastytreat.backend.tasty_treat_express_backend.services;

import com.tastyTreatExpress.DTO.ReportData;
import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ReportService {
    Report generateRestaurantReport(String restaurantId, LocalDate startDate, LocalDate endDate, String reportType);

    List<Report> getReportsByRestaurant(String restaurantId);

    List<Report> getReportsByType(String reportType);

    Map<String, Object> generateUserOrderSummaryReport(long userId);

    // new methods-
    // Core Report Generation
    Report generateReport(ReportRequest request, String reportType);

    // Report Retrieval
    List<Report> getReportsByCriteria(LocalDate startDate, LocalDate endDate, String reportType, int page, int size);

    List<Report> getAllReports();

    Report getReportById(Long id);

    // Report Persistence
    Report saveReport(ReportRequest request, String reportType);

    Report updateReport(Long id, ReportData updateData);
    
     Report updateReport2(Long reportId, Report updatedReportData);

    String exportReportsToCSV(List<Report> reports);

    // Report Deletion
    void deleteReport(Long id);

    void deleteAllReports();

    // Scheduled Report Generation
    void generateDailyReports();

    void generateWeeklyReports();

    void generateMonthlyReports();

    void sendReportByEmail(String recipientEmail, String subject, String message, byte[] attachmentData,
            String fileName);

    // Order Metrics
    Map<String, Object> generateOrderTrendReport(LocalDate startDate, LocalDate endDate);

    Map<String, Long> generatePeakOrderTimes(String restaurantId, LocalDate startDate, LocalDate endDate);

    // Feedback Analysis
    Map<String, Object> generateCustomerFeedbackSummary(String restaurantId, LocalDate startDate,
            LocalDate endDate);

    Map<String, Object> generateLowRatingAnalysis(String restaurantId, LocalDate startDate, LocalDate endDate);

    // Customer Engagement
    Map<String, Object> generateLoyalCustomerReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> generateCustomerChurnReport(LocalDate startDate, LocalDate endDate);

    // Operational Metrics
    Map<String, Object> generateOperationalEfficiencyReport(String restaurantId, LocalDate startDate,
            LocalDate endDate);

}
