package com.tastytreat.frontend.tasty_treat_express_frontend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity

public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    private Integer totalOrders;
    private Integer completed;
    private Integer pending;
    private Integer cancelled;
    private Double totalValue;
    private String mostOrderedItem;
   

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public String getMostOrderedItem() {
        return mostOrderedItem;
    }

    public void setMostOrderedItem(String mostOrderedItem) {
        this.mostOrderedItem = mostOrderedItem;
    }
    public Report(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor for ReportData-like usage
    public Report(Integer totalOrders, Integer completed, Integer pending, Integer cancelled, Double totalValue, String mostOrderedItem) {
        this.totalOrders = totalOrders;
        this.completed = completed;
        this.pending = pending;
        this.cancelled = cancelled;
        this.totalValue = totalValue;
        this.mostOrderedItem = mostOrderedItem;
    }

    // Constructor for full initialization
    public Report(LocalDate startDate, LocalDate endDate, Integer totalOrders, Integer completed, Integer pending, Integer cancelled, Double totalValue, String mostOrderedItem, LocalDateTime generatedAt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalOrders = totalOrders;
        this.completed = completed;
        this.pending = pending;
        this.cancelled = cancelled;
        this.totalValue = totalValue;
        this.mostOrderedItem = mostOrderedItem;
        this.generatedAt = generatedAt;
    }
   
}

