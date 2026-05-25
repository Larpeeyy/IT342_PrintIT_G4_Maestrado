package com.printit.backend.features.staff.orders;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{orderId}/download")
    public ResponseEntity<?> downloadOrderFile(@PathVariable Long orderId) {
        StaffOrderDownloadFile downloadFile = staffOrderService.getDownloadFile(orderId);

        if (downloadFile.isRedirect()) {
            return ResponseEntity
                    .status(302)
                    .header(HttpHeaders.LOCATION, downloadFile.getRedirectUrl())
                    .build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, downloadFile.getContentDisposition())
                .body(downloadFile.getResource());
    }

    @PutMapping("/{orderId}/status")
    public StaffOrderDetailsResponse updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStaffOrderStatusRequest request
    ) {
        return staffOrderService.updateOrderStatus(orderId, request);
    }
}
