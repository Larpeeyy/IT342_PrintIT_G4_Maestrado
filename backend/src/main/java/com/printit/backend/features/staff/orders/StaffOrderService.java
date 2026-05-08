package com.printit.backend.features.staff.orders;

import com.printit.backend.core.entity.Payment;
import com.printit.backend.core.entity.PrintOrder;
import com.printit.backend.core.repository.PaymentRepository;
import com.printit.backend.core.repository.PrintOrderRepository;
import com.printit.backend.features.notifications.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffOrderService {

    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public StaffOrderService(
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository,
            NotificationService notificationService
    ) {
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    public List<StaffOrderSummaryResponse> getAllOrders() {
        return printOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public StaffOrderDetailsResponse getOrderById(Long orderId) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        return mapToDetails(order);
    }

    public StaffOrderDetailsResponse updateOrderStatus(
            Long orderId,
            UpdateStaffOrderStatusRequest request
    ) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        String newStatus = normalizeStatus(request.getStatus());

        order.setStatus(newStatus);
        PrintOrder savedOrder = printOrderRepository.save(order);

        updateRelatedPayment(savedOrder, newStatus);

        notificationService.createNotification(
                savedOrder.getStudent(),
                "Order Status Updated",
                "Your order " + savedOrder.getOrderCode() + " is now " + savedOrder.getStatus() + ".",
                "ORDER_STATUS"
        );

        return mapToDetails(savedOrder);
    }

    private void updateRelatedPayment(PrintOrder order, String orderStatus) {
        Optional<Payment> existingPayment = paymentRepository.findByOrder(order);

        if (existingPayment.isEmpty()) {
            return;
        }

        Payment payment = existingPayment.get();

        if ("Completed".equalsIgnoreCase(orderStatus)) {
            payment.setStatus("Completed");
        } else {
            payment.setStatus("Pending");
        }

        if (order.getTotalAmount() != null) {
            payment.setAmount(order.getTotalAmount());
        }

        paymentRepository.save(payment);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status is required.");
        }

        if ("Pending".equalsIgnoreCase(status)) {
            return "Pending";
        }

        if ("Printing".equalsIgnoreCase(status)) {
            return "Printing";
        }

        if ("Ready for Pickup".equalsIgnoreCase(status)) {
            return "Ready for Pickup";
        }

        if ("Completed".equalsIgnoreCase(status)) {
            return "Completed";
        }

        throw new RuntimeException("Invalid order status.");
    }

    private StaffOrderSummaryResponse mapToSummary(PrintOrder order) {
        return new StaffOrderSummaryResponse(
                order.getId(),
                order.getOrderCode(),
                order.getFileName(),
                order.getStudent() != null ? order.getStudent().getFullName() : "Unknown",
                order.getCopies(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private StaffOrderDetailsResponse mapToDetails(PrintOrder order) {
        return new StaffOrderDetailsResponse(
                order.getId(),
                order.getOrderCode(),
                order.getStudent() != null ? order.getStudent().getFullName() : "Unknown",
                order.getStudent() != null ? order.getStudent().getEmail() : "Unknown",
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