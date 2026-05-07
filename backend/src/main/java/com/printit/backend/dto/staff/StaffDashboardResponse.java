package com.printit.backend.dto.staff;

import java.util.List;

public class StaffDashboardResponse {

    private long pendingOrders;
    private long printingOrders;
    private long readyForPickupOrders;
    private long completedToday;
    private List<StaffOrderSummaryResponse> recentOrders;

    public StaffDashboardResponse(
            long pendingOrders,
            long printingOrders,
            long readyForPickupOrders,
            long completedToday,
            List<StaffOrderSummaryResponse> recentOrders
    ) {
        this.pendingOrders = pendingOrders;
        this.printingOrders = printingOrders;
        this.readyForPickupOrders = readyForPickupOrders;
        this.completedToday = completedToday;
        this.recentOrders = recentOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public long getPrintingOrders() {
        return printingOrders;
    }

    public long getReadyForPickupOrders() {
        return readyForPickupOrders;
    }

    public long getCompletedToday() {
        return completedToday;
    }

    public List<StaffOrderSummaryResponse> getRecentOrders() {
        return recentOrders;
    }
}