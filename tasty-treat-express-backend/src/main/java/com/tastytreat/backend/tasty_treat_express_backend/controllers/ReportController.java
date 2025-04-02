package com.tastytreat.backend.tasty_treat_express_backend.controllers;

import com.tastyTreatExpress.DTO.ReportDTO;
import com.tastyTreatExpress.DTO.ReportMapper;
import com.tastyTreatExpress.DTO.ReportRequest;
import com.tastytreat.backend.tasty_treat_express_backend.exceptions.MainExceptionClass.InvalidInputException;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @GetMapping("/criteria/{startDate}/{endDate}/{reportType}/{page}/{size}")
    public ResponseEntity<List<ReportDTO>> getReportsByCriteria(
            @PathVariable String startDate,
            @PathVariable String endDate,
            @PathVariable String reportType,
            @PathVariable int page,
            @PathVariable int size) {

        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);

            if (start.isAfter(end)) {
                throw new InvalidInputException("Start date cannot be after end date.");
            }

            List<Report> reports = reportService.getReportsByCriteria(start, end, reportType, page, size);

            if (reports.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<ReportDTO> reportDTOs = reports.stream()
                    .map(ReportMapper::toReportDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(reportDTOs);

        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Use YYYY-MM-DD.");
        }
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
        try {
            reportService.deleteReport(reportId);
            return ResponseEntity.ok("Report deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found.");
        }
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
    @PostMapping("/send-report/{email}/{reportFormat}")
    public ResponseEntity<String> sendReport(
            @PathVariable String email,
            @PathVariable String reportFormat) throws IOException {

        if (!email.contains("@")) {
            throw new InvalidInputException("Invalid email format: " + email);
        }

        List<Report> reports = reportService.getAllReports();
        String fileName = "reports." + reportFormat;

        if ("csv".equalsIgnoreCase(reportFormat)) {
            byte[] csvData = reportService.exportReportsToCSV(reports).getBytes();
            reportService.sendReportByEmail(email, "Your Reports", "Attached report file.", csvData, fileName);

        } else {
            throw new InvalidInputException("Invalid report format. Use 'csv' or 'pdf'.");
        }
        return ResponseEntity.ok("Report sent successfully to " + email);
    }

}
