package com.printit.backend.service.staff;

import com.printit.backend.dto.staff.StaffDashboardResponse;
import com.printit.backend.dto.staff.StaffOrderSummaryResponse;
import com.printit.backend.entity.student.PrintOrder;
import com.printit.backend.repository.student.PrintOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffDashboardService {

    private final PrintOrderRepository printOrderRepository;

    public StaffDashboardService(PrintOrderRepository printOrderRepository) {
        this.printOrderRepository = printOrderRepository;
    }

    public StaffDashboardResponse getDashboardSummary() {
        long pendingOrders = printOrderRepository.countByStatus("Pending");
        long printingOrders = printOrderRepository.countByStatus("Printing");
        long readyForPickupOrders = printOrderRepository.countByStatus("Ready for Pickup");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        long completedToday = printOrderRepository.countByStatusAndCreatedAtBetween(
                "Completed",
                startOfDay,
                endOfDay
        );

        List<StaffOrderSummaryResponse> recentOrders = printOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(5)
                .map(this::mapToSummary)
                .toList();

        return new StaffDashboardResponse(
                pendingOrders,
                printingOrders,
                readyForPickupOrders,
                completedToday,
                recentOrders
        );
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
}