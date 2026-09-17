package com.petcare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/** Dùng khi Pet Owner request lịch hẹn, hoặc Veterinarian tự tạo lịch hẹn cho một pet owner. */
@Data
public class AppointmentRequest {
    @NotNull
    private Long petOwnerId;
    @NotNull
    private Long veterinarianId;
    @NotNull
    private Long petId;
    @NotNull
    private LocalDateTime appointmentTime;
}
