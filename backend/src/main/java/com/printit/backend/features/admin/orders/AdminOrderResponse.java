package com.printit.backend.features.admin.orders;

public class AdminOrderResponse {

    private Long id;
    private String fileName;
    private String studentName;
    private String dateSubmitted;
    private Long copies;
    private String status;

    public AdminOrderResponse(
            Long id,
            String fileName,
            String studentName,
            String dateSubmitted,
            Long copies,
            String status
    ) {
        this.id = id;
        this.fileName = fileName;
        this.studentName = studentName;
        this.dateSubmitted = dateSubmitted;
        this.copies = copies;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public Long getCopies() {
        return copies;
    }

    public String getStatus() {
        return status;
    }
}