package com.printit.backend.features.admin.dashboard;

public class PendingStaffResponse {

    private Long id;
    private String fullName;
    private String email;
    private String staffId;
    private String approvalStatus;

    public PendingStaffResponse(
            Long id,
            String fullName,
            String email,
            String staffId,
            String approvalStatus
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.staffId = staffId;
        this.approvalStatus = approvalStatus;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }
}