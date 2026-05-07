package com.printit.backend.service.staff;

import com.printit.backend.dto.admin.AdminPaymentResponse;
import com.printit.backend.entity.student.Payment;
import com.printit.backend.repository.student.PaymentRepository;
import com.printit.backend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffPaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public StaffPaymentService(
            PaymentRepository paymentRepository,
            NotificationService notificationService
    ) {
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    public List<AdminPaymentResponse> getPendingPayments() {
        return paymentRepository.findByStatusOrderByCreatedAtDesc("Pending")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AdminPaymentResponse markPaymentAsPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found."));

        payment.setStatus("Completed");
        Payment savedPayment = paymentRepository.save(payment);

        notificationService.createNotification(
                savedPayment.getOrder().getStudent(),
                "Payment Completed",
                "Payment for order " + savedPayment.getOrder().getOrderCode() + " has been marked as completed.",
                "PAYMENT"
        );

        return mapToResponse(savedPayment);
    }

    private AdminPaymentResponse mapToResponse(Payment payment) {
        return new AdminPaymentResponse(
                payment.getId(),
                payment.getPaymentCode(),
                payment.getOrder().getOrderCode(),
                payment.getOrder().getStudent() != null ? payment.getOrder().getStudent().getFullName() : "Unknown",
                payment.getOrder().getFileName(),
                payment.getProvider(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCreatedAt()
        );
    }
}