package com.printit.backend.dto.student;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {

    private long totalOrders;
    private long pendingOrders;
    private long readyForPickupOrders;
    private BigDecimal totalSpent;
    private List<OrderResponse> recentOrders;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(
            long totalOrders,
            long pendingOrders,
            long readyForPickupOrders,
            BigDecimal totalSpent,
            List<OrderResponse> recentOrders
    ) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.readyForPickupOrders = readyForPickupOrders;
        this.totalSpent = totalSpent;
        this.recentOrders = recentOrders;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public long getReadyForPickupOrders() {
        return readyForPickupOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public List<OrderResponse> getRecentOrders() {
        return recentOrders;
    }
}