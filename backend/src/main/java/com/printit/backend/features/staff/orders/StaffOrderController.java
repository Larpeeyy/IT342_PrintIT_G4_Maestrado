package com.printit.backend.features.staff.orders;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffOrderController {

    private final StaffOrderService staffOrderService;

    public StaffOrderController(StaffOrderService staffOrderService) {
        this.staffOrderService = staffOrderService;
    }

    @GetMapping
    public List<StaffOrderSummaryResponse> getOrders() {
        return staffOrderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public StaffOrderDetailsResponse getOrderById(@PathVariable Long orderId) {
        return staffOrderService.getOrderById(orderId);
    }

    @PutMapping("/{orderId}/status")
    public StaffOrderDetailsResponse updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStaffOrderStatusRequest request
    ) {
        return staffOrderService.updateOrderStatus(orderId, request);
    }
}