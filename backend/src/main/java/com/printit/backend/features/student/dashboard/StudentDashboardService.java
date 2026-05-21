package com.printit.backend.features.student.dashboard;

import com.printit.backend.features.student.orders.OrderResponse;
import com.printit.backend.core.entity.User;
import com.printit.backend.core.entity.PrintOrder;
import com.printit.backend.core.repository.UserRepository;
import com.printit.backend.core.repository.PaymentRepository;
import com.printit.backend.core.repository.PrintOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDashboardService {

    private final UserRepository userRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;

    public StudentDashboardService(
            UserRepository userRepository,
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardSummaryResponse getDashboardByStudentEmail(String email) {
        User student = getStudentByEmail(email);

        List<PrintOrder> studentOrders =
                printOrderRepository.findByStudentOrderByCreatedAtDesc(student);

        int totalOrders = studentOrders.size();

        int pendingOrders = (int) studentOrders.stream()
                .filter(order -> "Pending".equalsIgnoreCase(order.getStatus()))
                .count();

        int readyForPickupOrders = (int) studentOrders.stream()
                .filter(order -> "Ready for Pickup".equalsIgnoreCase(order.getStatus()))
                .count();

        BigDecimal totalSpent =
                paymentRepository.sumCompletedOrderAmountsByStudent(student);

        List<OrderResponse> recentOrders = studentOrders.stream()
                .limit(3)
                .map(this::mapToOrderResponse)
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

        User user = existingUser.orElseThrow(
                () -> new RuntimeException("Student account not found.")
        );

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only student accounts can access student dashboard.");
        }

        return user;
    }

    private OrderResponse mapToOrderResponse(PrintOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getFileName(),
                order.getPaperSize(),
                order.getColorMode(),
                order.getCopies(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}