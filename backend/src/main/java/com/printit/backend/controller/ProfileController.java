package com.printit.backend.controller;

import com.printit.backend.dto.ChangePasswordRequest;
import com.printit.backend.dto.ProfileResponse;
import com.printit.backend.dto.UpdateProfileRequest;
import com.printit.backend.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(@RequestParam String email) {
        return profileService.getProfileByEmail(email);
    }

    @PutMapping
    public ProfileResponse updateProfile(@RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(request);
    }

    @PutMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        profileService.changePassword(request);
        return "Password updated successfully.";
    }
}