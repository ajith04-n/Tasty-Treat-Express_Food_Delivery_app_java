package com.tastytreatexpress.tastytreatexpress.report;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.controllers.ReportController;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.tastytreat.backend.tasty_treat_express_backend.services.ReportService;

@WebMvcTest(ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private Report sampleReport;

    @BeforeEach
    public void setup() {
        sampleReport = new Report();
        sampleReport.setId(1L);
        sampleReport.setStartDate(LocalDate.of(2023, 1, 1));
        sampleReport.setEndDate(LocalDate.of(2023, 1, 7));
        sampleReport.setTotalOrders(100);
        sampleReport.setCompletedOrders(80);
        sampleReport.setPendingOrders(10);
        sampleReport.setCancelledOrders(10);
        sampleReport.setTotalOrderValue(5000.0);
        sampleReport.setBestSellingItem("Pizza");
        sampleReport.setReportType("WEEKLY");
    }

    @Test
    public void testGenerateReport() throws Exception {
        ReportRequest request = new ReportRequest(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 7));
        Mockito.when(reportService.saveReport(Mockito.any(ReportRequest.class), Mockito.anyString()))
                .thenReturn(sampleReport);

        mockMvc.perform(post("/api/reports/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"startDate\":\"2023-01-01\",\"endDate\":\"2023-01-07\"}")
                .param("reportType", "WEEKLY"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalOrders").value(100))
                .andExpect(jsonPath("$.bestSellingItem").value("Pizza"));

        Mockito.verify(reportService, Mockito.times(1)).saveReport(Mockito.any(ReportRequest.class),
                Mockito.eq("WEEKLY"));
    }
}

