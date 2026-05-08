package com.printit.backend.features.profile;

import com.printit.backend.features.auth.ChangePasswordRequest;
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