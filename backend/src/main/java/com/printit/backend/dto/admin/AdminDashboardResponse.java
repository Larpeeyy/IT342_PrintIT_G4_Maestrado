package com.printit.backend.dto.admin;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long approvedStaff;
    private long pendingStaff;
    private List<PendingStaffResponse> pendingStaffRequests;
    private List<ChartPointResponse> ordersPerDay;
    private List<ChartPointResponse> revenuePerMonth;

    public AdminDashboardResponse(
            long totalUsers,
            long totalStudents,
            long approvedStaff,
            long pendingStaff,
            List<PendingStaffResponse> pendingStaffRequests,
            List<ChartPointResponse> ordersPerDay,
            List<ChartPointResponse> revenuePerMonth
    ) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.approvedStaff = approvedStaff;
        this.pendingStaff = pendingStaff;
        this.pendingStaffRequests = pendingStaffRequests;
        this.ordersPerDay = ordersPerDay;
        this.revenuePerMonth = revenuePerMonth;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getApprovedStaff() {
        return approvedStaff;
    }

    public long getPendingStaff() {
        return pendingStaff;
    }

    public List<PendingStaffResponse> getPendingStaffRequests() {
        return pendingStaffRequests;
    }

    public List<ChartPointResponse> getOrdersPerDay() {
        return ordersPerDay;
    }

    public List<ChartPointResponse> getRevenuePerMonth() {
        return revenuePerMonth;
    }

    public static class ChartPointResponse {
        private String label;
        private BigDecimal value;

        public ChartPointResponse(String label, BigDecimal value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public BigDecimal getValue() {
            return value;
        }
    }
}