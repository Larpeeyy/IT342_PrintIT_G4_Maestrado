package com.printit.backend.dto.staff;

import java.time.LocalDateTime;

public class StaffOrderSummaryResponse {

    private Long id;
    private String orderCode;
    private String fileName;
    private String studentName;
    private Integer copies;
    private String status;
    private LocalDateTime createdAt;

    public StaffOrderSummaryResponse(
            Long id,
            String orderCode,
            String fileName,
            String studentName,
            Integer copies,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.orderCode = orderCode;
        this.fileName = fileName;
        this.studentName = studentName;
        this.copies = copies;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getCopies() {
        return copies;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}