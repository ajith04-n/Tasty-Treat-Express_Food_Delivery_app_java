// package com.ordermenu.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import com.ordermenu.model.Report;
// import com.ordermenu.service.ReportService;

// import java.util.List;

// @Controller
// @RequestMapping("/reports")
// public class ReportController {

//     @Autowired
//     private ReportService reportService;

//     // Show Report List
//     @GetMapping
//     public String listReports(Model model) {
//         List<Report> reports = reportService.getAllReports();
//         model.addAttribute("reports", reports);
//         return "report_list"; // Corresponds to report_list.html
//     }

//     // Generate Report Page
//     @GetMapping("/generate")
//     public String showGenerateReportForm() {
//         return "generate_report"; // Corresponds to generate_report.html
//     }

//     // Process Report Generation
//     @PostMapping("/generate")
//     public String generateReport(@ModelAttribute Report report) {
//         reportService.saveReport(report);
//         return "redirect:/reports";
//     }

//     // Edit Report Page
//     @GetMapping("/edit/{id}")
//     public String showEditForm(@PathVariable Long id, Model model) {
//         Report report = reportService.getReportById(id);
//         model.addAttribute("report", report);
//         return "edit_report"; // Corresponds to edit_report.html
//     }

//     // Update Report
//     @PostMapping("/update")
//     public String updateReport(@ModelAttribute Report report) {
//         reportService.updateReport(report);
//         return "redirect:/reports";
//     }

//     // Delete Single Report
//     @PostMapping("/delete/{id}")
//     public String deleteReport(@PathVariable Long id) {
//         reportService.deleteReportById(id);
//         return "redirect:/reports";
//     }

//     // Delete All Reports
//     @PostMapping("/deleteAll")
//     public String deleteAllReports() {
//         reportService.deleteAllReports();
//         return "redirect:/reports";
//     }
// }
