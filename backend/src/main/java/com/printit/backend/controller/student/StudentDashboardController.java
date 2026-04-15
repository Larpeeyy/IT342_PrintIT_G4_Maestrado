package com.printit.backend.controller.student;

import com.printit.backend.dto.student.DashboardSummaryResponse;
import com.printit.backend.service.student.StudentDashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    public StudentDashboardController(StudentDashboardService studentDashboardService) {
        this.studentDashboardService = studentDashboardService;
    }

    @GetMapping
    public DashboardSummaryResponse getDashboard(@RequestParam String email) {
        return studentDashboardService.getDashboardSummary(email);
    }
}