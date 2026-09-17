package com.petcare.repository;

import com.petcare.entity.Appointment;
import com.petcare.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPetOwnerId(Long petOwnerId);
    List<Appointment> findByVeterinarianId(Long veterinarianId);

    List<Appointment> findByVeterinarianIdAndStatus(Long veterinarianId, AppointmentStatus status);

    List<Appointment> findByPetOwnerIdAndStatusAndCancelledAtBetween(
            Long petOwnerId, AppointmentStatus status, LocalDateTime from, LocalDateTime to);

    List<Appointment> findByVeterinarianIdAndAppointmentTimeBetween(
            Long veterinarianId, LocalDateTime from, LocalDateTime to);
}
