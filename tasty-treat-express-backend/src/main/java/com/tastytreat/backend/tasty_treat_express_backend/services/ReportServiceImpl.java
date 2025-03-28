package com.tastytreat.backend.tasty_treat_express_backend.services;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tastyTreatExpress.DTO.ReportData;
import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.ReportNotFoundException;
import com.tastytreat.backend.tasty_treat_express_backend.models.Feedback;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.FeedbackRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.ReportRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.RestaurantRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    EmailService emailService;

    @Override
    public Report generateRestaurantReport(String restaurantId, LocalDate startDate, LocalDate endDate,
            String reportType) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        // Orders data
        List<Order> orders = orderRepository.findByRestaurantRestaurantIdAndOrderDateBetween(restaurantId,
                startDate.atStartOfDay(), endDate.atTime(23, 59));
        int totalOrders = orders.size();
        int completedOrders = (int) orders.stream().filter(order -> order.getStatus().equalsIgnoreCase("Completed"))
                .count();
        int pendingOrders = (int) orders.stream().filter(order -> order.getStatus().equalsIgnoreCase("Pending"))
                .count();
        int cancelledOrders = (int) orders.stream().filter(order -> order.getStatus().equalsIgnoreCase("Cancelled"))
                .count();
        double totalOrderValue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        double averageOrderValue = totalOrders > 0 ? totalOrderValue / totalOrders : 0;

        // Customer data
        Set<Long> uniqueCustomers = orders.stream().map(order -> order.getUser().getId())
                .collect(Collectors.toSet());
        int newCustomers = (int) orders.stream()
                .filter(order -> order.getUser().getCreatedAt().toLocalDate().isAfter(startDate)).count();
        double repeatCustomerRate = uniqueCustomers.isEmpty() ? 0
                : (double) (uniqueCustomers.size() - newCustomers) / uniqueCustomers.size() * 100;

        // Feedback data
        List<Feedback> feedbacks = feedbackRepository.findByRestaurantRestaurantId(restaurantId).stream()
                .filter(feedback -> feedback.getFeedbackDate().toLocalDate().isAfter(startDate) &&
                        feedback.getFeedbackDate().toLocalDate().isBefore(endDate))
                .collect(Collectors.toList());
        double averageFeedbackRating = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
        int positiveFeedbackCount = (int) feedbacks.stream().filter(feedback -> feedback.getRating() >= 4).count();
        int negativeFeedbackCount = (int) feedbacks.stream().filter(feedback -> feedback.getRating() <= 2).count();

        Map<Integer, Long> feedbackRatingDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));

        // Menu metrics
        Map<String, Long> menuItemPopularity = new HashMap<>();
        orders.forEach(order -> {
            order.getMenuItems().forEach(menuItem -> {
                menuItemPopularity.put(menuItem.getName(), menuItemPopularity.getOrDefault(menuItem.getName(), 0L) + 1);
            });
        });

        // Get the best-selling item
        String bestSellingItem = menuItemPopularity.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No items sold");

        // Delivery data
        double averageDeliveryTime = orders.stream()
                .mapToDouble(order -> ChronoUnit.MINUTES.between(order.getOrderDate(), order.getDeliveryTime()))
                .average()
                .orElse(0.0);
        double delayRate = orders.stream()
                .filter(order -> order.getDeliveryTime().isAfter(order.getDeliveryTime()))
                .count() * 100.0 / totalOrders;

        // Build and save the report
        Report report = new Report(startDate, endDate, LocalDateTime.now(), totalOrders, completedOrders,
                pendingOrders, cancelledOrders, totalOrderValue, averageOrderValue, uniqueCustomers.size(),
                repeatCustomerRate, newCustomers, averageFeedbackRating, positiveFeedbackCount,
                negativeFeedbackCount, feedbackRatingDistribution, menuItemPopularity, bestSellingItem,
                averageDeliveryTime, 0.0, delayRate, null, reportType, null, restaurant);

        return reportRepository.save(report);
    }

    @Override
    public List<Report> getReportsByRestaurant(String restaurantId) {
        return reportRepository.findByRestaurantRestaurantId(restaurantId);
    }

    @Override
    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    @Override
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
    }

    @Override
    public Map<String, Object> generateUserOrderSummaryReport(long userId) {

        List<Order> userOrders = orderRepository.findByUser_Id(userId);

        Map<String, Object> response = new HashMap<>();
        if (userOrders.isEmpty()) {
            response.put("userId", userId);
            response.put("totalOrders", 0);
            response.put("completedOrders", 0);
            response.put("pendingOrders", 0);
            response.put("totalRevenue", 0.0);
            response.put("latestOrderDate", null);
            response.put("averageOrderValue", 0.0);
            response.put("orders", Collections.emptyList());
            return response;
        }

        int totalOrders = userOrders.size();
        int completedOrders = (int) userOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus()))
                .count();
        int pendingOrders = (int) userOrders.stream()
                .filter(order -> "PLACED".equals(order.getStatus()))
                .count();
        double totalRevenue = userOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getStatus()) || "PLACED".equals(order.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        LocalDateTime latestOrderDate = userOrders.stream()
                .map(Order::getOrderDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        // Populate the response map
        response.put("userId", userId);
        response.put("totalOrders", totalOrders);
        response.put("completedOrders", completedOrders);
        response.put("pendingOrders", pendingOrders);
        response.put("totalRevenue", totalRevenue);
        response.put("latestOrderDate", latestOrderDate);
        response.put("averageOrderValue", averageOrderValue);
        response.put("orders", userOrders);

        // Save to the database
        User user = userOrders.get(0).getUser();
        Report report = new Report();
        report.setUser(user);
        report.setTotalOrders(totalOrders);
        report.setCompletedOrders(completedOrders);
        report.setPendingOrders(pendingOrders);
        report.setTotalOrderValue(totalRevenue);
        report.setAverageOrderValue(averageOrderValue);
        report.setLatestOrderDate(latestOrderDate);
        report.setReportType("USER_SUMMARY");
        report.setGeneratedAt(LocalDateTime.now());

        reportRepository.save(report);

        return response;
    }

    @Override
    public Report generateReport(ReportRequest request, String reportType) {
    	
        
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().atTime(23, 59, 59);

        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);

        ReportData reportData = new ReportData(
                orders.size(),
                orderRepository.countByStatusAndOrderDateBetween("COMPLETED", start, end).intValue(),
                orderRepository.countByStatusAndOrderDateBetween("PENDING", start, end).intValue(),
                orderRepository.countByStatusAndOrderDateBetween("CANCELLED", start, end).intValue(),
                orders.stream().mapToDouble(Order::getTotalAmount).sum(),
                findMostOrderedItem(orders));

        Report report = new Report();
        
        Restaurant restaurant = restaurantRepository.findById(reportType)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        report.setRestaurant(restaurant);
        report.setStartDate(request.getStartDate());
        report.setEndDate(request.getEndDate());
        report.setGeneratedAt(LocalDateTime.now());
        report.setTotalOrders(reportData.getTotalOrders());
        report.setCompletedOrders(reportData.getCompleted());
        report.setPendingOrders(reportData.getPending());
        report.setCancelledOrders(reportData.getCancelled());
        report.setTotalOrderValue(reportData.getTotalValue());
        report.setBestSellingItem(reportData.getMostOrderedItem());
        report.setReportType(reportType);

        return reportRepository.save(report);
    }

    @Override
    public List<Report> getReportsByCriteria(LocalDate startDate, LocalDate endDate, String reportType, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportRepository.findByStartDateBetweenAndReportType(startDate, endDate, reportType, pageable);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report saveReport(ReportRequest request, String reportType) {
        return generateReport(request, reportType);
    }

    @Override
    public Report updateReport(Long id, ReportData updateData) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));
        report.setTotalOrders(updateData.getTotalOrders());
        report.setCompletedOrders(updateData.getCompleted());
        report.setPendingOrders(updateData.getPending());
        report.setCancelledOrders(updateData.getCancelled());
        report.setTotalOrderValue(updateData.getTotalValue());
        report.setBestSellingItem(updateData.getMostOrderedItem());
        return reportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public void deleteAllReports() {
        reportRepository.deleteAll();
    }

    @Override
    public void generateDailyReports() {
        LocalDate today = LocalDate.now();
        ReportRequest request = new ReportRequest(today.minusDays(1), today);
        saveReport(request, "DAILY");
    }

    @Override
    public void generateWeeklyReports() {
        LocalDate today = LocalDate.now();
        ReportRequest request = new ReportRequest(today.minusWeeks(1), today);
        saveReport(request, "WEEKLY");
    }

    @Override
    public void generateMonthlyReports() {
        LocalDate today = LocalDate.now();
        ReportRequest request = new ReportRequest(today.minusMonths(1), today);
        saveReport(request, "MONTHLY");
    }

    private String findMostOrderedItem(List<Order> orders) {
        return orders.stream()
                .map(Order::getMostOrderedItem)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No items ordered");
    }

    public String exportReportsToCSV(List<Report> reports) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(
                "ID,Start Date,End Date,Total Orders,Completed Orders,Pending Orders,Cancelled Orders,Total Value,Most Ordered Item\n");

        for (Report report : reports) {
            csvBuilder.append(report.getId()).append(",");
            csvBuilder.append(report.getStartDate()).append(",");
            csvBuilder.append(report.getEndDate()).append(",");
            csvBuilder.append(report.getTotalOrders()).append(",");
            csvBuilder.append(report.getCompletedOrders()).append(",");
            csvBuilder.append(report.getPendingOrders()).append(",");
            csvBuilder.append(report.getCancelledOrders()).append(",");
            csvBuilder.append(report.getTotalOrderValue()).append(",");
            csvBuilder.append(report.getBestSellingItem()).append("\n");
        }
        return csvBuilder.toString();
    }
    

    @Override
    public void sendReportByEmail(String recipientEmail, String subject, String message, byte[] attachmentData,
            String fileName) {
        emailService.sendReportByEmail(recipientEmail, subject, message, attachmentData, fileName);
    }



    public Report updateReport2(Long reportId, Report updatedReportData) {
        // Fetch the existing report from the database
        Report existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report with ID " + reportId + " not found."));

        // Update the fields in the existing report with the new data
        if (updatedReportData.getStartDate() != null) {
            existingReport.setStartDate(updatedReportData.getStartDate());
        }
        if (updatedReportData.getEndDate() != null) {
            existingReport.setEndDate(updatedReportData.getEndDate());
        }
        if (updatedReportData.getTotalOrders() != null) {
            existingReport.setTotalOrders(updatedReportData.getTotalOrders());
        }
        if (updatedReportData.getCompletedOrders() != null) {
            existingReport.setCompletedOrders(updatedReportData.getCompletedOrders());
        }
        if (updatedReportData.getPendingOrders() != null) {
            existingReport.setPendingOrders(updatedReportData.getPendingOrders());
        }
        if (updatedReportData.getCancelledOrders() != null) {
            existingReport.setCancelledOrders(updatedReportData.getCancelledOrders());
        }
        if (updatedReportData.getTotalOrderValue() != null) {
            existingReport.setTotalOrderValue(updatedReportData.getTotalOrderValue());
        }
        if (updatedReportData.getAverageOrderValue() != null) {
            existingReport.setAverageOrderValue(updatedReportData.getAverageOrderValue());
        }
        if (updatedReportData.getLatestOrderDate() != null) {
            existingReport.setLatestOrderDate(updatedReportData.getLatestOrderDate());
        }
        if (updatedReportData.getUniqueCustomers() != null) {
            existingReport.setUniqueCustomers(updatedReportData.getUniqueCustomers());
        }
        if (updatedReportData.getRepeatCustomerRate() != null) {
            existingReport.setRepeatCustomerRate(updatedReportData.getRepeatCustomerRate());
        }
        if (updatedReportData.getNewCustomers() != null) {
            existingReport.setNewCustomers(updatedReportData.getNewCustomers());
        }
        if (updatedReportData.getAverageFeedbackRating() != null) {
            existingReport.setAverageFeedbackRating(updatedReportData.getAverageFeedbackRating());
        }
        if (updatedReportData.getPositiveFeedbackCount() != null) {
            existingReport.setPositiveFeedbackCount(updatedReportData.getPositiveFeedbackCount());
        }
        if (updatedReportData.getNegativeFeedbackCount() != null) {
            existingReport.setNegativeFeedbackCount(updatedReportData.getNegativeFeedbackCount());
        }
        if (updatedReportData.getBestSellingItem() != null) {
            existingReport.setBestSellingItem(updatedReportData.getBestSellingItem());
        }
        if (updatedReportData.getAverageDeliveryTime() != null) {
            existingReport.setAverageDeliveryTime(updatedReportData.getAverageDeliveryTime());
        }
        if (updatedReportData.getEstimatedDeliveryTime() != null) {
            existingReport.setEstimatedDeliveryTime(updatedReportData.getEstimatedDeliveryTime());
        }
        if (updatedReportData.getDelayRate() != null) {
            existingReport.setDelayRate(updatedReportData.getDelayRate());
        }
        if (updatedReportData.getOutOfStockCount() != null) {
            existingReport.setOutOfStockCount(updatedReportData.getOutOfStockCount());
        }

        // Update relationships if necessary (e.g., User or Restaurant)
        if (updatedReportData.getUser() != null) {
            existingReport.setUser(updatedReportData.getUser());
        }
        if (updatedReportData.getRestaurant() != null) {
            existingReport.setRestaurant(updatedReportData.getRestaurant());
        }

        // Save the updated report back to the database
        return reportRepository.save(existingReport);
    }

    @Override
    public Map<String, Object> generateOrderTrendReport(LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateOrderTrendReport'");
    }

    @Override
    public Map<String, Long> generatePeakOrderTimes(String restaurantId, LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generatePeakOrderTimes'");
    }

    @Override
    public Map<String, Object> generateCustomerFeedbackSummary(String restaurantId, LocalDate startDate,
            LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateCustomerFeedbackSummary'");
    }

    @Override
    public Map<String, Object> generateLowRatingAnalysis(String restaurantId, LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateLowRatingAnalysis'");
    }

    @Override
    public Map<String, Object> generateLoyalCustomerReport(LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateLoyalCustomerReport'");
    }

    @Override
    public Map<String, Object> generateCustomerChurnReport(LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateCustomerChurnReport'");
    }

    @Override
    public Map<String, Object> generateOperationalEfficiencyReport(String restaurantId, LocalDate startDate,
            LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateOperationalEfficiencyReport'");
    }

}
