package com.printit.backend.features.student.orders;

import com.printit.backend.core.entity.Payment;
import com.printit.backend.core.entity.PrintOrder;
import com.printit.backend.core.entity.User;
import com.printit.backend.core.repository.PaymentRepository;
import com.printit.backend.core.repository.PrintOrderRepository;
import com.printit.backend.core.repository.UserRepository;
import com.printit.backend.features.notifications.NotificationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentOrderService {

    private final UserRepository userRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public StudentOrderService(
            UserRepository userRepository,
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository,
            NotificationService notificationService
    ) {
        this.userRepository = userRepository;
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrderRequest(request);

        User student = getStudentByEmail(request.getEmail());

        PrintOrder order = new PrintOrder();
        order.setOrderCode(generateOrderCode());
        order.setFileName(request.getFileName().trim());
        order.setFileUrl(cleanOptionalText(request.getFileUrl()));
        order.setPaperSize(request.getPaperSize().trim());
        order.setColorMode(normalizeColorMode(request.getColorMode()));
        order.setCopies(request.getCopies());
        order.setStatus("Pending");
        order.setTotalAmount(calculateAmount(
                request.getPaperSize(),
                request.getColorMode(),
                request.getCopies()
        ));
        order.setCreatedAt(LocalDateTime.now());
        order.setStudent(student);

        PrintOrder savedOrder = printOrderRepository.save(order);

        Payment payment = new Payment();
        payment.setPaymentCode(generatePaymentCode());
        payment.setOrder(savedOrder);
        payment.setProvider("Sandbox");
        payment.setAmount(savedOrder.getTotalAmount());
        payment.setStatus("Pending");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        notificationService.createNotification(
                student,
                "Order Submitted",
                "Your print order " + savedOrder.getOrderCode() + " was submitted successfully.",
                "ORDER"
        );

        List<User> staffUsers = userRepository.findByRoleAndApprovalStatus("STAFF", "APPROVED");

        for (User staff : staffUsers) {
            notificationService.createNotification(
                    staff,
                    "New Print Order",
                    "A new order " + savedOrder.getOrderCode() + " was submitted by " + student.getFullName() + ".",
                    "ORDER"
            );
        }

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByStudentEmail(String email) {
        User student = getStudentByEmail(email);

        return printOrderRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByIdAndStudentEmail(Long orderId, String email) {
        User student = getStudentByEmail(email);

        PrintOrder order = printOrderRepository.findByIdAndStudent(orderId, student)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        return mapToOrderResponse(order);
    }

    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Student email is required.");
        }

        if (request.getFileName() == null || request.getFileName().isBlank()) {
            throw new RuntimeException("File name is required.");
        }

        String lowerFileName = request.getFileName().toLowerCase(Locale.ROOT);

        if (!(lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".docx"))) {
            throw new RuntimeException("Only PDF and DOCX files are allowed.");
        }

        if (request.getFileUrl() == null || request.getFileUrl().isBlank()) {
            throw new RuntimeException("Uploaded file URL is required.");
        }

        if (request.getPaperSize() == null || request.getPaperSize().isBlank()) {
            throw new RuntimeException("Paper size is required.");
        }

        if (request.getColorMode() == null || request.getColorMode().isBlank()) {
            throw new RuntimeException("Color mode is required.");
        }

        if (request.getCopies() == null || request.getCopies() < 1) {
            throw new RuntimeException("Copies must be at least 1.");
        }
    }

    private User getStudentByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = existingUser.orElseThrow(() -> new RuntimeException("Student account not found."));

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Only student accounts can create student print orders.");
        }

        return user;
    }

    private BigDecimal calculateAmount(String paperSize, String colorMode, Integer copies) {
        BigDecimal base;

        switch (paperSize.toUpperCase(Locale.ROOT)) {
            case "LETTER":
            case "SHORT":
                base = BigDecimal.valueOf(5);
                break;

            case "LEGAL":
            case "LONG":
                base = BigDecimal.valueOf(6);
                break;

            default:
                base = BigDecimal.valueOf(4);
                break;
        }

        BigDecimal colorExtra;

        if ("Color".equalsIgnoreCase(colorMode)) {
            colorExtra = BigDecimal.valueOf(3);
        } else {
            colorExtra = BigDecimal.ZERO;
        }

        return base.add(colorExtra).multiply(BigDecimal.valueOf(copies));
    }

    private String normalizeColorMode(String colorMode) {
        if ("Color".equalsIgnoreCase(colorMode)) {
            return "Color";
        }

        return "Black & White";
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String generateOrderCode() {
        long count = printOrderRepository.count() + 1;
        return String.format("ORD-%d-%03d", LocalDateTime.now().getYear(), count);
    }

    private String generatePaymentCode() {
        long count = paymentRepository.count() + 1;
        return String.format("PAY-%d-%03d", LocalDateTime.now().getYear(), count);
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
