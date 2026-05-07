package com.printit.backend.service.admin;

import com.printit.backend.dto.admin.AdminDashboardResponse;
import com.printit.backend.dto.admin.AdminOrderResponse;
import com.printit.backend.dto.admin.AdminPaymentResponse;
import com.printit.backend.dto.admin.AdminUserResponse;
import com.printit.backend.dto.admin.PendingStaffResponse;
import com.printit.backend.entity.User;
import com.printit.backend.entity.student.Payment;
import com.printit.backend.entity.student.PrintOrder;
import com.printit.backend.repository.UserRepository;
import com.printit.backend.repository.student.PaymentRepository;
import com.printit.backend.repository.student.PrintOrderRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
    }

    public AdminDashboardResponse getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole("STUDENT");
        long approvedStaff = userRepository.countByRoleAndApprovalStatus("STAFF", "APPROVED");
        long pendingStaff = userRepository.countByRoleAndApprovalStatus("STAFF", "PENDING");

        List<PendingStaffResponse> pendingStaffRequests =
                userRepository.findByRoleAndApprovalStatusOrderByIdDesc("STAFF", "PENDING")
                        .stream()
                        .map(this::mapToPendingStaffResponse)
                        .toList();

        return new AdminDashboardResponse(
                totalUsers,
                totalStudents,
                approvedStaff,
                pendingStaff,
                pendingStaffRequests
        );
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToAdminUserResponse)
                .toList();
    }

    public List<AdminOrderResponse> getAllOrders() {
        return printOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToAdminOrderResponse)
                .toList();
    }

    public List<AdminPaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToAdminPaymentResponse)
                .toList();
    }

    public void approveStaff(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!"STAFF".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Selected account is not a staff account.");
        }

        user.setApprovalStatus("APPROVED");
        userRepository.save(user);
    }

    public void rejectStaff(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!"STAFF".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Selected account is not a staff account.");
        }

        user.setApprovalStatus("REJECTED");
        userRepository.save(user);
    }

    private PendingStaffResponse mapToPendingStaffResponse(User user) {
        return new PendingStaffResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStaffId(),
                user.getApprovalStatus()
        );
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getApprovalStatus(),
                user.getUsername()
        );
    }

    private AdminOrderResponse mapToAdminOrderResponse(PrintOrder order) {
        String dateSubmitted = order.getCreatedAt() != null
                ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                : "";

        return new AdminOrderResponse(
                order.getId(),
                order.getFileName(),
                order.getStudent() != null ? order.getStudent().getFullName() : "Unknown",
                dateSubmitted,
                order.getCopies() != null ? order.getCopies().longValue() : 0L,
                order.getStatus()
        );
    }

    private AdminPaymentResponse mapToAdminPaymentResponse(Payment payment) {
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