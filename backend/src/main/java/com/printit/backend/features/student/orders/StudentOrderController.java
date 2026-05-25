package com.printit.backend.features.student.orders;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student/orders")
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://127.0.0.1:3000"
        },
        allowCredentials = "true"
)
public class StudentOrderController {

    private final StudentOrderService studentOrderService;
    private final StudentOrderFileStorageService fileStorageService;

    public StudentOrderController(
            StudentOrderService studentOrderService,
            StudentOrderFileStorageService fileStorageService
    ) {
        this.studentOrderService = studentOrderService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public OrderFileUploadResponse uploadOrderFile(
            @RequestParam("file") MultipartFile file
    ) {
        return fileStorageService.uploadOrderFile(file);
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return studentOrderService.createOrder(request);
    }

    @PostMapping("/with-file")
    public OrderResponse createOrderWithFile(
            @RequestParam("email") String email,
            @RequestParam("file") MultipartFile file,
            @RequestParam("paperSize") String paperSize,
            @RequestParam("colorMode") String colorMode,
            @RequestParam("copies") Integer copies
    ) {
        OrderFileUploadResponse upload = fileStorageService.uploadOrderFile(file);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setEmail(email);
        request.setFileName(upload.getFileName());
        request.setFileUrl(upload.getFileUrl());
        request.setPaperSize(paperSize);
        request.setColorMode(colorMode);
        request.setCopies(copies);

        return studentOrderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam String email) {
        return studentOrderService.getOrdersByStudentEmail(email);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @PathVariable Long id,
            @RequestParam String email
    ) {
        return studentOrderService.getOrderByIdAndStudentEmail(id, email);
    }
}
