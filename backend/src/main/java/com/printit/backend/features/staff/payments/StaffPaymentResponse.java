package com.printit.backend.features.staff.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StaffPaymentResponse {

    private Long id;
    private String paymentCode;
    private String orderCode;
    private String studentName;
    private String fileName;
    private BigDecimal amount;
    private String status;
    private String provider;
    private LocalDateTime createdAt;

    public StaffPaymentResponse(
            Long id,
            String paymentCode,
            String orderCode,
            String studentName,
            String fileName,
            BigDecimal amount,
            String status,
            String provider,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.paymentCode = paymentCode;
        this.orderCode = orderCode;
        this.studentName = studentName;
        this.fileName = fileName;
        this.amount = amount;
        this.status = status;
        this.provider = provider;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getFileName() {
        return fileName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getProvider() {
        return provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}