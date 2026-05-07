package com.printit.backend.controller.admin;

import com.printit.backend.dto.admin.AdminDashboardResponse;
import com.printit.backend.dto.admin.AdminOrderResponse;
import com.printit.backend.dto.admin.AdminPaymentResponse;
import com.printit.backend.dto.admin.AdminUserResponse;
import com.printit.backend.service.admin.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminDashboardService.getDashboardSummary();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers() {
        return adminDashboardService.getAllUsers();
    }

    @GetMapping("/orders")
    public List<AdminOrderResponse> getOrders() {
        return adminDashboardService.getAllOrders();
    }

    @GetMapping("/payments")
    public List<AdminPaymentResponse> getPayments() {
        return adminDashboardService.getAllPayments();
    }

    @PutMapping("/staff/{userId}/approve")
    public String approveStaff(@PathVariable Long userId) {
        adminDashboardService.approveStaff(userId);
        return "Staff account approved successfully.";
    }

    @PutMapping("/staff/{userId}/reject")
    public String rejectStaff(@PathVariable Long userId) {
        adminDashboardService.rejectStaff(userId);
        return "Staff account rejected successfully.";
    }
}