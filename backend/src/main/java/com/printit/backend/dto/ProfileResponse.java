package com.printit.backend.dto;

public class ProfileResponse {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String studentId;
    private String staffId;
    private String profileImageUrl;

    public ProfileResponse() {
    }

    public ProfileResponse(
            Long id,
            String fullName,
            String username,
            String email,
            String role,
            String studentId,
            String staffId,
            String profileImageUrl
    ) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.studentId = studentId;
        this.staffId = staffId;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}