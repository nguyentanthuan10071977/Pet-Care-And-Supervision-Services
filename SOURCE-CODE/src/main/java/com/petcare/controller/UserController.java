package com.petcare.controller;

import com.petcare.dto.*;
import com.petcare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** User Story "View Pet Owner Profile" / "View profile" (Veterinarian/Manager). */
    @GetMapping("/{id}")
    public UserResponse getProfile(@PathVariable Long id) {
        return userService.getProfile(id);
    }

    /** User Story "Edit pet owner's profile" / "Edit profile" (Veterinarian). */
    @PutMapping("/{id}")
    public UserResponse updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(id, req);
    }

    /** User Story "View list Veterinarian". */
    @GetMapping("/veterinarians")
    public List<UserResponse> listVeterinarians() {
        return userService.listVeterinarians();
    }

    /** User Story "View list pet owners" (Veterinarian). */
    @GetMapping("/pet-owners")
    public List<UserResponse> listPetOwners() {
        return userService.listPetOwners();
    }

    /** User Story "Search Veterinarian or pets" - phần veterinarian. */
    @GetMapping("/veterinarians/search")
    public List<UserResponse> searchVeterinarians(@RequestParam String q) {
        return userService.searchVeterinarians(q);
    }
}
