package com.tastytreat.frontend.tasty_treat_express_frontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.tastytreat.frontend.tasty_treat_express_frontend.models.Report;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final String BASE_URL = "http://localhost:8080/api/reports"; // Change this as needed

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public String getAllReports(Model model) {
        ResponseEntity<List<Report>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<Report>>() {
                });
        model.addAttribute("reports", response.getBody());
        return "report_list";
    }

    @GetMapping("/{id}")
    public String getReportById(@PathVariable Long id, Model model) {
        Report report = restTemplate.getForObject(BASE_URL + "/" + id, Report.class);
        model.addAttribute("report", report);
        return "report_detail";
    }

    @GetMapping("/generate")
    public String showGenerateReportForm(Model model, HttpSession session) {
        String resId = (String) session.getAttribute("restaurantId");
        model.addAttribute("resId", resId);
        return "report";
    }

    @PostMapping("/generate")
    public String generateReport(@ModelAttribute Report report) {
        restTemplate.postForObject(BASE_URL + "/generate", report, Report.class);
        return "redirect:/reports";
    }

    @PostMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id) {
        restTemplate.delete(BASE_URL + "/delete/" + id);
        return "redirect:/reports"; // Redirect to report list after deletion
    }

    @PostMapping("/deleteAll")
    public String deleteAllReports() {
        restTemplate.delete(BASE_URL + "/deleteAll");
        return "redirect:/reports";
    }
}
