package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefuseAppointmentRequest {
    @NotBlank
    private String reason;
}
