package com.petcare.controller;

import com.petcare.dto.NotificationResponse;
import com.petcare.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** User Story: View notifications / Delete a notification. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> list(@RequestParam Long userId) {
        return notificationService.listForUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
