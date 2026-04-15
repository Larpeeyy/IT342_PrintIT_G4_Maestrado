package com.printit.backend.dto.student;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private String orderCode;
    private String fileName;
    private String paperSize;
    private String colorMode;
    private Integer copies;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public OrderResponse() {
    }

    public OrderResponse(
            Long id,
            String orderCode,
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