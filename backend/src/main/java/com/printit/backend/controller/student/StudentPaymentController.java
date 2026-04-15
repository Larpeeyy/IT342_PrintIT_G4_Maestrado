package com.printit.backend.controller.student;

import com.printit.backend.dto.student.PaymentResponse;
import com.printit.backend.service.student.StudentPaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentPaymentController {

    private final StudentPaymentService studentPaymentService;

    public StudentPaymentController(StudentPaymentService studentPaymentService) {
        this.studentPaymentService = studentPaymentService;
    }

    @GetMapping
    public List<PaymentResponse> getPayments(@RequestParam String email) {
        return studentPaymentService.getPaymentsByStudentEmail(email);
    }
}