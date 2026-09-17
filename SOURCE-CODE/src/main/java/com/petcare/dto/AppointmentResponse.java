package com.petcare.dto;

import com.petcare.entity.Appointment;
import com.petcare.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long petOwnerId;
    private String petOwnerName;
    private Long veterinarianId;
    private String veterinarianName;
    private Long petId;
    private String petName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String refuseReason;

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(a.getId(),
                a.getPetOwner().getId(), a.getPetOwner().getFullName(),
                a.getVeterinarian().getId(), a.getVeterinarian().getFullName(),
                a.getPet().getId(), a.getPet().getName(),
                a.getAppointmentTime(), a.getStatus(), a.getRefuseReason());
    }
}
