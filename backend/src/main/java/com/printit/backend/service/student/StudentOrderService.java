package com.printit.backend.service.student;

import com.printit.backend.dto.student.CreateOrderRequest;
import com.printit.backend.dto.student.OrderResponse;
import com.printit.backend.entity.User;
import com.printit.backend.entity.student.Payment;
import com.printit.backend.entity.student.PrintOrder;
import com.printit.backend.repository.UserRepository;
import com.printit.backend.repository.student.PaymentRepository;
import com.printit.backend.repository.student.PrintOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class StudentOrderService {

    private final UserRepository userRepository;
    private final PrintOrderRepository printOrderRepository;
    private final PaymentRepository paymentRepository;

    public StudentOrderService(
            UserRepository userRepository,
            PrintOrderRepository printOrderRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.printOrderRepository = printOrderRepository;
        this.paymentRepository = paymentRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrderRequest(request);

        User student = getStudentByEmail(request.getEmail());

        PrintOrder order = new PrintOrder();
        order.setOrderCode(generateOrderCode());
        order.setFileName(request.getFileName().trim());
        order.setPaperSize(request.getPaperSize().trim());
        order.setColorMode(normalizeColorMode(request.getColorMode()));
        order.setCopies(request.getCopies());
        order.setStatus("Pending");
        order.setTotalAmount(calculateAmount(request.getPaperSize(), request.getColorMode(), request.getCopies()));
        order.setCreatedAt(LocalDateTime.now());
        order.setStudent(student);

        PrintOrder savedOrder = printOrderRepository.save(order);

        Payment payment = new Payment();
        payment.setPaymentCode(generatePaymentCode());
        payment.setOrder(savedOrder);
        payment.setProvider("Sandbox");
        payment.setProviderPaymentId(null);
        payment.setAmount(savedOrder.getTotalAmount());
        payment.setCurrency("PHP");
        payment.setStatus("Pending");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByStudentEmail(String email) {
        User student = getStudentByEmail(email);
        return printOrderRepository.findByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
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

    private String normalizeColorMode(String colorMode) {
        if ("Color".equalsIgnoreCase(colorMode)) {
            return "Color";
        }
        return "Black & White";
    }

    private BigDecimal calculateAmount(String paperSize, String colorMode, Integer copies) {
        BigDecimal base = switch (paperSize.toUpperCase(Locale.ROOT)) {
            case "LETTER" -> BigDecimal.valueOf(5);
            case "LEGAL" -> BigDecimal.valueOf(6);
            default -> BigDecimal.valueOf(4);
        };

        BigDecimal colorExtra = "Color".equalsIgnoreCase(colorMode)
                ? BigDecimal.valueOf(3)
                : BigDecimal.ZERO;

        return base.add(colorExtra).multiply(BigDecimal.valueOf(copies));
    }

    private String generateOrderCode() {
        long nextValue = printOrderRepository.count() + 1;
        return String.format("ORD-2026-%03d", nextValue);
    }

    private String generatePaymentCode() {
        long nextValue = paymentRepository.count() + 1;
        return String.format("PAY-2026-%03d", nextValue);
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