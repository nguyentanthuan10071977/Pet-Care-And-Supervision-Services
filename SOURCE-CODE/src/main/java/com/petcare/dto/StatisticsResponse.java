package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** User Story: View Statistical / Export statistical (Manager). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private long totalPetOwners;
    private long totalVeterinarians;
    private long totalPets;
    private long totalFacilities;
    private long totalAppointments;
    private long pendingAppointments;
    private long acceptedAppointments;
    private long cancelledAppointments;
}
