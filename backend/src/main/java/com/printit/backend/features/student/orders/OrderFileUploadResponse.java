package com.printit.backend.features.student.orders;

public class OrderFileUploadResponse {

    private String fileName;
    private String fileUrl;

    public OrderFileUploadResponse(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}