package com.printit.backend.features.staff.orders;

public class UpdateStaffOrderStatusRequest {

    private String status;
    private String notes;

    public UpdateStaffOrderStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}