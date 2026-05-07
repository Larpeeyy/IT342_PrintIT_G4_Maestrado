package com.printit.backend.entity.student;

import com.printit.backend.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "print_orders")
public class PrintOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "paper_size")
    private String paperSize;

    @Column(name = "color_mode")
    private String colorMode;

    @Column(name = "copies")
    private Integer copies;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "Pending";
        }
    }
}