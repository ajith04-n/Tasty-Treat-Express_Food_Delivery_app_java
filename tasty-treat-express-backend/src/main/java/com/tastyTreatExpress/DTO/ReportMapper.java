package com.tastyTreatExpress.DTO;

import com.tastytreat.backend.tasty_treat_express_backend.models.Report;

public class ReportMapper {

    public static ReportDTO toReportDTO(Report report) {
        if (report == null)
            return null;

        return new ReportDTO(
                report.getId(),
                report.getStartDate(),
                report.getEndDate(),
                report.getGeneratedAt(),
                report.getTotalOrders(),
                report.getCompletedOrders(),
                report.getPendingOrders(),
                report.getCancelledOrders(),
                report.getTotalOrderValue(),
                report.getAverageOrderValue(),
                report.getLatestOrderDate(),
                report.getUniqueCustomers(),
                report.getRepeatCustomerRate(),
                report.getNewCustomers(),
                report.getAverageFeedbackRating(),
                report.getPositiveFeedbackCount(),
                report.getNegativeFeedbackCount(),
                report.getBestSellingItem(),
                report.getAverageDeliveryTime(),
                report.getEstimatedDeliveryTime(),
                report.getDelayRate(),
                report.getOutOfStockCount(),
                report.getUser() != null ? report.getUser().getId() : null,
                report.getRestaurant() != null ? report.getRestaurant().getRestaurantId() : null);
    }

    public static Report toReportEntity(ReportDTO reportDTO) {
        if (reportDTO == null)
            return null;

        Report report = new Report();
        report.setId(reportDTO.getId());
        report.setStartDate(reportDTO.getStartDate());
        report.setEndDate(reportDTO.getEndDate());
        report.setGeneratedAt(reportDTO.getGeneratedAt());
        report.setTotalOrders(reportDTO.getTotalOrders());
        report.setCompletedOrders(reportDTO.getCompletedOrders());
        report.setPendingOrders(reportDTO.getPendingOrders());
        report.setCancelledOrders(reportDTO.getCancelledOrders());
        report.setTotalOrderValue(reportDTO.getTotalOrderValue());
        report.setAverageOrderValue(reportDTO.getAverageOrderValue());
        report.setLatestOrderDate(reportDTO.getLatestOrderDate());
        report.setUniqueCustomers(reportDTO.getUniqueCustomers());
        report.setRepeatCustomerRate(reportDTO.getRepeatCustomerRate());
        report.setNewCustomers(reportDTO.getNewCustomers());
        report.setAverageFeedbackRating(reportDTO.getAverageFeedbackRating());
        report.setPositiveFeedbackCount(reportDTO.getPositiveFeedbackCount());
        report.setNegativeFeedbackCount(reportDTO.getNegativeFeedbackCount());
        report.setBestSellingItem(reportDTO.getBestSellingItem());
        report.setAverageDeliveryTime(reportDTO.getAverageDeliveryTime());
        report.setEstimatedDeliveryTime(reportDTO.getEstimatedDeliveryTime());
        report.setDelayRate(reportDTO.getDelayRate());
        report.setOutOfStockCount(reportDTO.getOutOfStockCount());

       
        return report;
    }
}
