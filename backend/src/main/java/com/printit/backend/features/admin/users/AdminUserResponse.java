package com.printit.backend.features.admin.users;

public class AdminUserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private String approvalStatus;

    public AdminUserResponse(
            Long id,
            String fullName,
            String email,
            String username,
            String role,
            String approvalStatus
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }
}