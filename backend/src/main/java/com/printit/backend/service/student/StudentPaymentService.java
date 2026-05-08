package com.printit.backend.service.student;

import com.printit.backend.dto.student.PaymentResponse;
import com.printit.backend.entity.User;
import com.printit.backend.entity.student.Payment;
import com.printit.backend.entity.student.PrintOrder;
import com.printit.backend.repository.UserRepository;
import com.printit.backend.repository.student.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StudentPaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public StudentPaymentService(
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentResponse> getPaymentsByStudentEmail(String email) {
        User student = getStudentByEmail(email);

        return paymentRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    private User getStudentByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = existingUser.orElseThrow(
                () -> new RuntimeException("Student account not found.")
        );

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only student accounts can access student payment records.");
        }

        return user;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PrintOrder order = payment.getOrder();

        String latestStatus = payment.getStatus();
        BigDecimal correctAmount = payment.getAmount();

        if (order != null) {
            if (order.getStatus() != null) {
                latestStatus = order.getStatus();
            }

            if (order.getTotalAmount() != null) {
                correctAmount = order.getTotalAmount();
            }
        }

        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentCode(),
                order != null ? order.getOrderCode() : "-",
                order != null ? order.getFileName() : "-",
                payment.getProvider(),
                latestStatus,
                correctAmount,
                payment.getCreatedAt()
        );
    }
}