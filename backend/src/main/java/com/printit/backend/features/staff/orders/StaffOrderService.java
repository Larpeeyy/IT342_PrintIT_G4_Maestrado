package com.printit.backend.features.staff.orders;

import com.printit.backend.core.entity.Payment;
import com.printit.backend.core.entity.PrintOrder;
import com.printit.backend.core.repository.PaymentRepository;
import com.printit.backend.core.repository.PrintOrderRepository;
import com.printit.backend.features.notifications.NotificationService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffOrderService {

    private static final String PRINT_ORDER_UPLOAD_DIR = "uploads/print-orders";

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
                .collect(Collectors.toList());
    }

    public StaffOrderDetailsResponse getOrderById(Long orderId) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        return mapToDetails(order);
    }

    public StaffOrderDownloadFile getDownloadFile(Long orderId) {
        PrintOrder order = printOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        if (order.getFileUrl() == null || order.getFileUrl().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "File download is not available because this order has no saved file URL."
            );
        }

        if (!isLocalPrintOrderFileUrl(order.getFileUrl())) {
            return new StaffOrderDownloadFile(order.getFileUrl());
        }

        try {
            Path uploadPath = Paths.get(PRINT_ORDER_UPLOAD_DIR)
                    .toAbsolutePath()
                    .normalize();

            String storedFileName = extractStoredFileName(order.getFileUrl());

            Path filePath = uploadPath
                    .resolve(storedFileName)
                    .normalize();

            if (!filePath.startsWith(uploadPath) || !Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uploaded file was not found.");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uploaded file is not readable.");
            }

            String contentType = Files.probeContentType(filePath);

            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            String downloadName = order.getFileName() != null && !order.getFileName().isBlank()
                    ? order.getFileName()
                    : filePath.getFileName().toString();

            String contentDisposition = ContentDisposition.attachment()
                    .filename(downloadName, StandardCharsets.UTF_8)
                    .build()
                    .toString();

            return new StaffOrderDownloadFile(resource, contentType, contentDisposition);
        } catch (ResponseStatusException error) {
            throw error;
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to download file.");
        }
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

    private String extractStoredFileName(String fileUrl) {
        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();

            if (path != null && !path.isBlank()) {
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                return URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException ignored) {
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        return URLDecoder.decode(fileName, StandardCharsets.UTF_8);
    }

    private boolean isLocalPrintOrderFileUrl(String fileUrl) {
        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();
            return path != null && path.contains("/api/files/print-orders/");
        } catch (IllegalArgumentException ignored) {
            return fileUrl.contains("/api/files/print-orders/");
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
                order.getFileUrl(),
                order.getPaperSize(),
                order.getColorMode(),
                order.getCopies(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}
