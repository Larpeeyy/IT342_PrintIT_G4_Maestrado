package com.printit.backend.service.staff;

import com.printit.backend.dto.staff.StaffOrderDetailsResponse;
import com.printit.backend.dto.staff.StaffOrderSummaryResponse;
import com.printit.backend.dto.staff.UpdateStaffOrderStatusRequest;
import com.printit.backend.entity.student.PrintOrder;
import com.printit.backend.repository.student.PrintOrderRepository;
import com.printit.backend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffOrderService {

    private final PrintOrderRepository printOrderRepository;
    private final NotificationService notificationService;

    public StaffOrderService(
            PrintOrderRepository printOrderRepository,
            NotificationService notificationService
    ) {
        this.printOrderRepository = printOrderRepository;
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

    public StaffOrderDetailsResponse updateOrderStatus(Long orderId, UpdateStaffOrderStatusRequest request) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        validateStatus(request.getStatus());
        order.setStatus(request.getStatus());

        PrintOrder savedOrder = printOrderRepository.save(order);

        notificationService.createNotification(
                savedOrder.getStudent(),
                "Order Status Updated",
                "Your order " + savedOrder.getOrderCode() + " is now " + savedOrder.getStatus() + ".",
                "ORDER_STATUS"
        );

        return mapToDetails(savedOrder);
    }

    private void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status is required.");
        }

        boolean valid =
                "Pending".equalsIgnoreCase(status) ||
                        "Printing".equalsIgnoreCase(status) ||
                        "Ready for Pickup".equalsIgnoreCase(status) ||
                        "Completed".equalsIgnoreCase(status);

        if (!valid) {
            throw new RuntimeException("Invalid order status.");
        }
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