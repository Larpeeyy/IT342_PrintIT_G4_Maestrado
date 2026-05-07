package com.printit.backend.dto.admin;

import java.util.List;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long approvedStaff;
    private long pendingStaff;
    private List<PendingStaffResponse> pendingStaffRequests;

    public AdminDashboardResponse(
            long totalUsers,
            long totalStudents,
            long approvedStaff,
            long pendingStaff,
            List<PendingStaffResponse> pendingStaffRequests
    ) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.approvedStaff = approvedStaff;
        this.pendingStaff = pendingStaff;
        this.pendingStaffRequests = pendingStaffRequests;
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
}