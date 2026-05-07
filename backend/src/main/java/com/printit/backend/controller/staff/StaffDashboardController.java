package com.printit.backend.controller.staff;

import com.printit.backend.dto.staff.StaffDashboardResponse;
import com.printit.backend.service.staff.StaffDashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffDashboardController {

    private final StaffDashboardService staffDashboardService;

    public StaffDashboardController(StaffDashboardService staffDashboardService) {
        this.staffDashboardService = staffDashboardService;
    }

    @GetMapping
    public StaffDashboardResponse getDashboard() {
        return staffDashboardService.getDashboardSummary();
    }
}