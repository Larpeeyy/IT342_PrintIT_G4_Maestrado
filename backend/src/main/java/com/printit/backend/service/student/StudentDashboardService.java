package com.printit.backend.service.student;

import com.printit.backend.dto.student.DashboardSummaryResponse;
import com.printit.backend.dto.student.OrderResponse;
import com.printit.backend.entity.User;
import com.printit.backend.repository.UserRepository;
import com.printit.backend.repository.student.PaymentRepository;
import com.printit.backend.repository.student.PrintOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDashboardService {

    private final UserRepository userRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;
    private final StudentOrderService studentOrderService;

    public StudentDashboardService(
            UserRepository userRepository,
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository,
            StudentOrderService studentOrderService
    ) {
        this.userRepository = userRepository;
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
        this.studentOrderService = studentOrderService;
    }

    public DashboardSummaryResponse getDashboardSummary(String email) {
        User student = getStudentByEmail(email);

        long totalOrders = printOrderRepository.countByStudent(student);
        long pendingOrders = printOrderRepository.countByStudentAndStatus(student, "Pending");
        long readyForPickupOrders = printOrderRepository.countByStudentAndStatus(student, "Ready for Pickup");

        BigDecimal totalSpent = paymentRepository.sumCompletedPaymentsByStudent(student);
        List<OrderResponse> recentOrders = studentOrderService.getOrdersByStudentEmail(email)
                .stream()
                .limit(3)
                .toList();

        return new DashboardSummaryResponse(
                totalOrders,
                pendingOrders,
                readyForPickupOrders,
                totalSpent,
                recentOrders
        );
    }

    private User getStudentByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = existingUser.orElseThrow(() -> new RuntimeException("Student account not found."));

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only student accounts can access the student dashboard.");
        }

        return user;
    }
}