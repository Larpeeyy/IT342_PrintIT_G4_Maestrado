package com.printit.backend.features.admin.dashboard;

import com.printit.backend.core.entity.Payment;
import com.printit.backend.core.entity.PrintOrder;
import com.printit.backend.core.entity.User;
import com.printit.backend.core.repository.PaymentRepository;
import com.printit.backend.core.repository.PrintOrderRepository;
import com.printit.backend.core.repository.UserRepository;
import com.printit.backend.features.admin.orders.AdminOrderResponse;
import com.printit.backend.features.admin.payments.AdminPaymentResponse;
import com.printit.backend.features.admin.users.AdminUserResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                        .collect(Collectors.toList());

        List<AdminDashboardResponse.ChartPointResponse> ordersPerDay =
                getOrdersPerDayChart();

        List<AdminDashboardResponse.ChartPointResponse> revenuePerMonth =
                getRevenuePerMonthChart();

        return new AdminDashboardResponse(
                totalUsers,
                totalStudents,
                approvedStaff,
                pendingStaff,
                pendingStaffRequests,
                ordersPerDay,
                revenuePerMonth
        );
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    public List<AdminOrderResponse> getAllOrders() {
        return printOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToAdminOrderResponse)
                .collect(Collectors.toList());
    }

    public List<AdminPaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToAdminPaymentResponse)
                .collect(Collectors.toList());
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

    private List<AdminDashboardResponse.ChartPointResponse> getOrdersPerDayChart() {
        Map<DayOfWeek, Long> ordersByDay = new LinkedHashMap<>();

        ordersByDay.put(DayOfWeek.MONDAY, 0L);
        ordersByDay.put(DayOfWeek.TUESDAY, 0L);
        ordersByDay.put(DayOfWeek.WEDNESDAY, 0L);
        ordersByDay.put(DayOfWeek.THURSDAY, 0L);
        ordersByDay.put(DayOfWeek.FRIDAY, 0L);
        ordersByDay.put(DayOfWeek.SATURDAY, 0L);
        ordersByDay.put(DayOfWeek.SUNDAY, 0L);

        List<PrintOrder> orders = printOrderRepository.findAllByOrderByCreatedAtDesc();

        for (PrintOrder order : orders) {
            if (order.getCreatedAt() == null) {
                continue;
            }

            DayOfWeek day = order.getCreatedAt().getDayOfWeek();

            if (ordersByDay.containsKey(day)) {
                ordersByDay.put(day, ordersByDay.get(day) + 1);
            }
        }

        return ordersByDay.entrySet()
                .stream()
                .map(entry -> new AdminDashboardResponse.ChartPointResponse(
                        formatDayLabel(entry.getKey()),
                        BigDecimal.valueOf(entry.getValue())
                ))
                .collect(Collectors.toList());
    }

    private String formatDayLabel(DayOfWeek day) {
        if (day == null) {
            return "";
        }

        switch (day) {
            case MONDAY:
                return "Mon";
            case TUESDAY:
                return "Tue";
            case WEDNESDAY:
                return "Wed";
            case THURSDAY:
                return "Thu";
            case FRIDAY:
                return "Fri";
            case SATURDAY:
                return "Sat";
            case SUNDAY:
                return "Sun";
            default:
                return "";
        }
    }

    private List<AdminDashboardResponse.ChartPointResponse> getRevenuePerMonthChart() {
        YearMonth currentMonth = YearMonth.now();
        Map<YearMonth, BigDecimal> revenueByMonth = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            revenueByMonth.put(month, BigDecimal.ZERO);
        }

        List<Payment> payments = paymentRepository.findAllByOrderByCreatedAtDesc();

        for (Payment payment : payments) {
            if (payment.getCreatedAt() == null || payment.getOrder() == null) {
                continue;
            }

            if (!"Completed".equalsIgnoreCase(payment.getOrder().getStatus())) {
                continue;
            }

            YearMonth paymentMonth = YearMonth.from(payment.getCreatedAt());

            if (!revenueByMonth.containsKey(paymentMonth)) {
                continue;
            }

            BigDecimal amount = payment.getOrder().getTotalAmount() != null
                    ? payment.getOrder().getTotalAmount()
                    : payment.getAmount();

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            revenueByMonth.put(paymentMonth, revenueByMonth.get(paymentMonth).add(amount));
        }

        DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern("MMM");

        return revenueByMonth.entrySet()
                .stream()
                .map(entry -> new AdminDashboardResponse.ChartPointResponse(
                        entry.getKey().format(labelFormat),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
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
        BigDecimal correctAmount = payment.getAmount();

        if (payment.getOrder() != null && payment.getOrder().getTotalAmount() != null) {
            correctAmount = payment.getOrder().getTotalAmount();
        }

        if (correctAmount == null) {
            correctAmount = BigDecimal.ZERO;
        }

        return new AdminPaymentResponse(
                payment.getId(),
                payment.getPaymentCode(),
                payment.getOrder() != null ? payment.getOrder().getOrderCode() : "-",
                payment.getOrder() != null && payment.getOrder().getStudent() != null
                        ? payment.getOrder().getStudent().getFullName()
                        : "Unknown",
                payment.getOrder() != null ? payment.getOrder().getFileName() : "-",
                payment.getProvider(),
                payment.getOrder() != null ? payment.getOrder().getStatus() : payment.getStatus(),
                correctAmount,
                payment.getCreatedAt()
        );
    }
}