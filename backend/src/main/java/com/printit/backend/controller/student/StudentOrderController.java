package com.printit.backend.controller.student;

import com.printit.backend.dto.student.CreateOrderRequest;
import com.printit.backend.dto.student.OrderResponse;
import com.printit.backend.service.student.StudentOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentOrderController {

    private final StudentOrderService studentOrderService;

    public StudentOrderController(StudentOrderService studentOrderService) {
        this.studentOrderService = studentOrderService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return studentOrderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam String email) {
        return studentOrderService.getOrdersByStudentEmail(email);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id, @RequestParam String email) {
        return studentOrderService.getOrderByIdAndStudentEmail(id, email);
    }
}