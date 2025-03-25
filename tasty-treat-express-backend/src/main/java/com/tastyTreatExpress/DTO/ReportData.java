package com.tastyTreatExpress.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class ReportData {
    private Integer totalOrders;
    private Integer completed;
    private Integer pending;
    public ReportData(Integer totalOrders, Integer completed, Integer pending, Integer cancelled, Double totalValue, String mostOrderedItem) {
        this.totalOrders = totalOrders;
        this.completed = completed;
        this.pending = pending;
        this.cancelled = cancelled;
        this.totalValue = totalValue;
        this.mostOrderedItem = mostOrderedItem;
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

    private Integer cancelled;
    private Double totalValue;
    private String mostOrderedItem;
}