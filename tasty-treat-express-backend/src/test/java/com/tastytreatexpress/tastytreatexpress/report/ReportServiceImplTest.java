package com.tastytreatexpress.tastytreatexpress.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.tastyTreatExpress.DTO.ReportData;
import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.models.MenuItem;
import com.tastytreat.backend.tasty_treat_express_backend.models.Order;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.models.Restaurant;
import com.tastytreat.backend.tasty_treat_express_backend.models.User;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.OrderRepository;
import com.tastytreat.backend.tasty_treat_express_backend.repositories.ReportRepository;
import com.tastytreat.backend.tasty_treat_express_backend.services.ReportServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReportRepository reportRepository;

    private Report sampleReport;
    private ReportRequest reportRequest;

    @BeforeEach
    public void setup() {
       
        User customer = new User();
        customer.setId(1L);
        customer.setName("John Doe");

    
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId("R1");
        restaurant.setName("Test Restaurant");

       
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(1L);
        menuItem1.setName("Pizza");
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(2L);
        menuItem2.setName("Burger");
        List<MenuItem> menuItems = List.of(menuItem1, menuItem2);

       
        Order mockOrder = new Order(customer, restaurant, menuItems, 300.0, "COMPLETED",
                "123 Test St", "Paid", "Credit Card", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        mockOrder.setOrderId(1L);

        sampleReport = new Report();
        sampleReport.setId(1L);
        sampleReport.setStartDate(LocalDate.of(2023, 1, 1));
        sampleReport.setEndDate(LocalDate.of(2023, 1, 7));
        sampleReport.setTotalOrders(10);
        sampleReport.setCompletedOrders(8);
        sampleReport.setPendingOrders(2);
        sampleReport.setCancelledOrders(0);
        sampleReport.setTotalOrderValue(3000.0);
        sampleReport.setBestSellingItem("Pizza");
        sampleReport.setReportType("WEEKLY");

        reportRequest = new ReportRequest(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 7));
    }

    @Test
    public void testGenerateReport() {
        List<MenuItem> menuItems = List.of(
                new MenuItem("Pizza", "Cheesy and delicious", 12.99, "Italian", "pizza.jpg", 10),
                new MenuItem("Burger", "Juicy and flavorful", 8.99, "American", "burger.jpg", 20));

        List<Order> mockOrders = List.of(
                new Order(new User(), new Restaurant(), menuItems, 300.0, "COMPLETED",
                        "123 Main St", "Paid", "Credit Card", LocalDateTime.now().minusDays(2), LocalDateTime.now()),
                new Order(new User(), new Restaurant(), menuItems, 150.0, "PENDING",
                        "456 Main St", "Pending", "Cash", LocalDateTime.now().minusDays(1), null));

        Mockito.when(
                orderRepository.findByOrderDateBetween(Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(mockOrders);

        Report report = reportService.generateReport(reportRequest, "WEEKLY");

        Assertions.assertNotNull(report);
        Assertions.assertEquals(450.0, report.getTotalOrderValue());
        Assertions.assertEquals("Pizza", report.getBestSellingItem());
        Assertions.assertEquals(2, report.getCompletedOrders());
    }

    @Test
    public void testDeleteAllReports() {
        Mockito.doNothing().when(reportRepository).deleteAll();

        Assertions.assertDoesNotThrow(() -> reportService.deleteAllReports());
        Mockito.verify(reportRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    public void testDeleteReport() {
        Mockito.doNothing().when(reportRepository).deleteById(1L);

        Assertions.assertDoesNotThrow(() -> reportService.deleteReport(1L));
        Mockito.verify(reportRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateReport() {
        ReportData updateData = new ReportData(150, 120, 20, 10, 7500.0, "Burger");
        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.of(sampleReport));
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenReturn(sampleReport);

        Report updatedReport = reportService.updateReport(1L, updateData);

        Assertions.assertEquals(150, updatedReport.getTotalOrders());
        Assertions.assertEquals(7500.0, updatedReport.getTotalOrderValue());
        Assertions.assertEquals("Burger", updatedReport.getBestSellingItem());
        Mockito.verify(reportRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(reportRepository, Mockito.times(1)).save(Mockito.any(Report.class));
    }

    /*
    @Test
    public void testGetReportsByCriteria() {
        Page<Report> mockPage = new PageImpl<>(List.of(sampleReport));

        Mockito.when(reportRepository.findByStartDateBetweenAndReportType(
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.anyString(),
                Mockito.any(Pageable.class)))
                .thenReturn(mockPage);

        List<Report> reports = reportService.getReportsByCriteria(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 7),
                "WEEKLY",
                0,
                10);

        Assertions.assertEquals(1, reports.size());
        Assertions.assertEquals(sampleReport, reports.get(0));

     
        Mockito.verify(reportRepository, Mockito.times(1)).findByStartDateBetweenAndReportType(
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.anyString(),
                Mockito.any(Pageable.class));
    }
    */

    @Test
    public void testGetAllReports() {
        Mockito.when(reportRepository.findAll()).thenReturn(List.of(sampleReport));

        List<Report> reports = reportService.getAllReports();

        Assertions.assertEquals(1, reports.size());
        Assertions.assertEquals(sampleReport, reports.get(0));
        Mockito.verify(reportRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetReportById() {
        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.of(sampleReport));

        Report report = reportService.getReportById(1L);

        Assertions.assertNotNull(report);
        Assertions.assertEquals(1L, report.getId());
        Assertions.assertEquals("Pizza", report.getBestSellingItem());
        Mockito.verify(reportRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testSaveReport() {
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenReturn(sampleReport);
        Mockito.when(orderRepository.findByOrderDateBetween(Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Report savedReport = reportService.saveReport(reportRequest, "WEEKLY");

        Assertions.assertNotNull(savedReport);
        Assertions.assertEquals("WEEKLY", savedReport.getReportType());
        Assertions.assertEquals(0, savedReport.getTotalOrders());
        Assertions.assertEquals(0.0, savedReport.getTotalOrderValue());
        Mockito.verify(reportRepository, Mockito.times(1)).save(Mockito.any(Report.class));
    }

}