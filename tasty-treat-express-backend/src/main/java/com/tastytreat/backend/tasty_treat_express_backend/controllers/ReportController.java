package com.tastytreat.backend.tasty_treat_express_backend.controllers;


import com.tastyTreatExpress.DTO.ReportDTO;
import com.tastyTreatExpress.DTO.ReportMapper;
import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.models.Report;
import com.tastytreat.backend.tasty_treat_express_backend.services.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Generate a Report
    @PostMapping("/generate/{reportType}")
    public ResponseEntity<ReportDTO> generateReport(
            @RequestBody ReportRequest request,
            @PathVariable String reportType) {
    	
        Report report = reportService.saveReport(request, reportType);
        ReportDTO reportDTO = ReportMapper.toReportDTO(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportDTO);
    }

    // Get Report by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long reportId) {
        Report report = reportService.getReportById(reportId);
        if (report != null) {
            ReportDTO reportDTO = ReportMapper.toReportDTO(report);
            return ResponseEntity.ok(reportDTO);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get All Reports
    @GetMapping("/all")
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        List<ReportDTO> reportDTOs = reports.stream()
                .map(ReportMapper::toReportDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportDTOs);
    }

    // Get Reports by Criteria (Date Range, Type, Pagination)
    @GetMapping("/criteria")
    public ResponseEntity<List<ReportDTO>> getReportsByCriteria(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String reportType,
            @RequestParam int page,
            @RequestParam int size) {
        List<Report> reports = reportService.getReportsByCriteria(startDate, endDate, reportType, page, size);
        List<ReportDTO> reportDTOs = reports.stream()
                .map(ReportMapper::toReportDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reportDTOs);
    }

    // Update a Report
    @PutMapping("/{reportId}")
    public ResponseEntity<ReportDTO> updateReport(
            @PathVariable Long reportId,
            @RequestBody ReportDTO updateData) {
        Report updatedReport = reportService.updateReport2(reportId, ReportMapper.toReportEntity(updateData));
        ReportDTO updatedReportDTO = ReportMapper.toReportDTO(updatedReport);
        return ResponseEntity.ok(updatedReportDTO);
    }

    // Delete a Report by ID
    @DeleteMapping("/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("Report deleted successfully.");
    }

    // Delete All Reports
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllReports() {
        reportService.deleteAllReports();
        return ResponseEntity.ok("All reports deleted successfully.");
    }

    // Export Reports as CSV
    @GetMapping("/export/csv")
    public ResponseEntity<ByteArrayResource> exportReportsAsCSV() {
        List<Report> reports = reportService.getAllReports();
        String csvData = reportService.exportReportsToCSV(reports);
        ByteArrayResource resource = new ByteArrayResource(csvData.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    // Send Report via Email
    @PostMapping("/send-report")
    public ResponseEntity<String> sendReport(
            @RequestParam String email,
            @RequestParam String reportFormat) throws IOException {
        List<Report> reports = reportService.getAllReports();

        String fileName = "reports." + reportFormat;
        if ("csv".equalsIgnoreCase(reportFormat)) {
            String csvData = reportService.exportReportsToCSV(reports);
            reportService.sendReportByEmail(email, "Your Reports", "Please find the attached report.",
                    csvData.getBytes(), fileName);
        } else {
            return ResponseEntity.badRequest().body("Invalid report format. Use 'csv' or 'pdf'.");
        }

        return ResponseEntity.ok("Report sent successfully to " + email);
    }
}
