package com.printit.backend.features.admin.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminPaymentResponse {

    private Long id;
    private String paymentCode;
    private String orderCode;
    private String studentName;
    private String fileName;
    private String provider;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public AdminPaymentResponse(
            Long id,
            String paymentCode,
            String orderCode,
            String studentName,
            String fileName,
            String provider,
            String status,
            BigDecimal amount,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.paymentCode = paymentCode;
        this.orderCode = orderCode;
        this.studentName = studentName;
        this.fileName = fileName;
        this.provider = provider;
        this.status = status;
        this.amount = amount;
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

    public String getProvider() {
        return provider;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}