package com.petcare.controller;

import com.petcare.dto.*;
import com.petcare.service.AdminService;
import com.petcare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Story dành cho Manager: Manage/Create/Edit/Delete account,
 * Posts announcements, View/Export statistical, Admin profile.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    // ----- Manage account -----

    @GetMapping("/accounts")
    public List<UserResponse> listAccounts() {
        return userService.listAllAccounts();
    }

    @PostMapping("/accounts")
    public ResponseEntity<UserResponse> createAccount(@Valid @RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAccount(req));
    }

    @PutMapping("/accounts/{id}")
    public UserResponse editAccount(@PathVariable Long id, @RequestBody UpdateProfileRequest req) {
        return userService.editAccount(id, req);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        userService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Announcements -----

    @PostMapping("/announcements")
    public ResponseEntity<AnnouncementResponse> postAnnouncement(
            @Valid @RequestBody AnnouncementRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAnnouncement(req));
    }

    @GetMapping("/announcements")
    public List<AnnouncementResponse> listAnnouncements() {
        return adminService.listAnnouncements();
    }

    // ----- Statistics -----

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return adminService.statistics();
    }

    @GetMapping(value = "/statistics/export", produces = "text/csv")
    public ResponseEntity<String> exportStatistics() {
        String csv = adminService.exportStatisticsCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"statistics.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // ----- Admin profile -----

    @GetMapping("/profile/{id}")
    public UserResponse adminProfile(@PathVariable Long id) {
        return userService.getProfile(id);
    }
}
