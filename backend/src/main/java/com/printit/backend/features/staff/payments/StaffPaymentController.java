package com.printit.backend.features.staff.payments;

import com.printit.backend.features.admin.payments.AdminPaymentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffPaymentController {

    private final StaffPaymentService staffPaymentService;

    public StaffPaymentController(StaffPaymentService staffPaymentService) {
        this.staffPaymentService = staffPaymentService;
    }

    @GetMapping("/pending")
    public List<AdminPaymentResponse> getPendingPayments() {
        return staffPaymentService.getPendingPayments();
    }

    @PutMapping("/{paymentId}/mark-paid")
    public Map<String, String> markPaymentAsPaid(@PathVariable Long paymentId) {
        staffPaymentService.markPaymentAsPaid(paymentId);
        return Map.of("message", "Payment marked as completed.");
    }
}