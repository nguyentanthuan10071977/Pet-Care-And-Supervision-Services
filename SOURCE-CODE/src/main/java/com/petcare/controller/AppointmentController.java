package com.petcare.controller;

import com.petcare.dto.*;
import com.petcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /** User Story "Request an appointment" (Pet Owner). */
    @PostMapping("/request")
    public ResponseEntity<AppointmentResponse> request(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.request(req));
    }

    /** User Story "Create an appointment" (Veterinarian). */
    @PostMapping("/create-by-vet")
    public ResponseEntity<AppointmentResponse> createByVet(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createByVet(req));
    }

    /** User Story "Accept an Appointment" (Veterinarian). */
    @PutMapping("/{id}/accept")
    public AppointmentResponse accept(@PathVariable Long id) {
        return appointmentService.accept(id);
    }

    /** User Story "Refuse an appointment" (Veterinarian). */
    @PutMapping("/{id}/refuse")
    public AppointmentResponse refuse(@PathVariable Long id,
                                       @Valid @RequestBody RefuseAppointmentRequest req) {
        return appointmentService.refuse(id, req);
    }

    /** User Story "Cancel an appointment" (Pet Owner). */
    @PutMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id, @RequestParam Long petOwnerId) {
        return appointmentService.cancel(id, petOwnerId);
    }

    /** User Story "View appointments" (Pet Owner). */
    @GetMapping("/pet-owner/{petOwnerId}")
    public List<AppointmentResponse> listForPetOwner(@PathVariable Long petOwnerId) {
        return appointmentService.listForPetOwner(petOwnerId);
    }

    /** User Story "View appointments" (Veterinarian). */
    @GetMapping("/veterinarian/{vetId}")
    public List<AppointmentResponse> listForVeterinarian(@PathVariable Long vetId) {
        return appointmentService.listForVeterinarian(vetId);
    }

    /** User Story "View schedule" (Veterinarian). */
    @GetMapping("/veterinarian/{vetId}/schedule")
    public List<AppointmentResponse> schedule(@PathVariable Long vetId) {
        return appointmentService.schedule(vetId);
    }
}
