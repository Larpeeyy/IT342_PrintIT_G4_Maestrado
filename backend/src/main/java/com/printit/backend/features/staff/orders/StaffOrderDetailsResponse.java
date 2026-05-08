package com.printit.backend.features.staff.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StaffOrderDetailsResponse {

    private Long id;
    private String orderCode;
    private String studentName;
    private String studentEmail;
    private String fileName;
    private String paperSize;
    private String colorMode;
    private Integer copies;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public StaffOrderDetailsResponse(
            Long id,
            String orderCode,
            String studentName,
            String studentEmail,
            String fileName,
            String paperSize,
            String colorMode,
            Integer copies,
            String status,
            BigDecimal totalAmount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.orderCode = orderCode;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.fileName = fileName;
        this.paperSize = paperSize;
        this.colorMode = colorMode;
        this.copies = copies;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public String getColorMode() {
        return colorMode;
    }

    public Integer getCopies() {
        return copies;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}