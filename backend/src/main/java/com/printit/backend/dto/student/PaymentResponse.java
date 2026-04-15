package com.printit.backend.dto.student;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private String paymentCode;
    private String orderCode;
    private String fileName;
    private String provider;
    private String status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            String paymentCode,
            String orderCode,
            String fileName,
            String provider,
            String status,
            BigDecimal amount,
            String currency,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.paymentCode = paymentCode;
        this.orderCode = orderCode;
        this.fileName = fileName;
        this.provider = provider;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
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

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}