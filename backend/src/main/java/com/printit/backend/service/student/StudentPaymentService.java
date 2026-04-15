package com.printit.backend.service.student;

import com.printit.backend.dto.student.PaymentResponse;
import com.printit.backend.entity.User;
import com.printit.backend.entity.student.Payment;
import com.printit.backend.repository.UserRepository;
import com.printit.backend.repository.student.PaymentRepository;
import org.springframework.stereotype.Service;

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

        User user = existingUser.orElseThrow(() -> new RuntimeException("Student account not found."));

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only student accounts can access student payment records.");
        }

        return user;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentCode(),
                payment.getOrder().getOrderCode(),
                payment.getOrder().getFileName(),
                payment.getProvider(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCreatedAt()
        );
    }
}