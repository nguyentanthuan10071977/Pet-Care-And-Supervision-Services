package com.petcare.service;

import com.petcare.dto.AppointmentRequest;
import com.petcare.dto.AppointmentResponse;
import com.petcare.dto.RefuseAppointmentRequest;
import com.petcare.entity.*;
import com.petcare.exception.ApiException;
import com.petcare.repository.AppointmentRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * User Story: Request/Cancel appointment (Pet Owner),
 * Accept/Refuse/Create appointment, View schedule (Veterinarian),
 * View appointments (dùng chung cho cả 2 actor).
 */
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int MAX_CANCELLATIONS_PER_MONTH = 3;

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final NotificationService notificationService;

    /** Pet Owner request một lịch hẹn mới -> trạng thái PENDING. */
    @Transactional
    public AppointmentResponse request(AppointmentRequest req) {
        User petOwner = getUser(req.getPetOwnerId(), Role.PET_OWNER);
        User vet = getUser(req.getVeterinarianId(), Role.VETERINARIAN);
        var pet = petRepository.findById(req.getPetId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy thú cưng."));

        ensureNoTimeConflict(vet.getId(), req.getAppointmentTime());

        Appointment appointment = Appointment.builder()
                .petOwner(petOwner)
                .veterinarian(vet)
                .pet(pet)
                .appointmentTime(req.getAppointmentTime())
                .status(AppointmentStatus.PENDING)
                .build();
        appointmentRepository.save(appointment);

        notificationService.notify(vet, "Lịch hẹn mới",
                petOwner.getFullName() + " yêu cầu đặt lịch hẹn cho thú cưng " + pet.getName() +
                        " vào " + req.getAppointmentTime());

        return AppointmentResponse.from(appointment);
    }

    /** Veterinarian tự tạo lịch hẹn (không cần đi qua bước request/accept). */
    @Transactional
    public AppointmentResponse createByVet(AppointmentRequest req) {
        User petOwner = getUser(req.getPetOwnerId(), Role.PET_OWNER);
        User vet = getUser(req.getVeterinarianId(), Role.VETERINARIAN);
        var pet = petRepository.findById(req.getPetId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy thú cưng."));

        ensureNoTimeConflict(vet.getId(), req.getAppointmentTime());

        Appointment appointment = Appointment.builder()
                .petOwner(petOwner)
                .veterinarian(vet)
                .pet(pet)
                .appointmentTime(req.getAppointmentTime())
                .status(AppointmentStatus.ACCEPTED)
                .build();
        appointmentRepository.save(appointment);

        notificationService.notify(petOwner, "Lịch hẹn được tạo",
                "Bác sĩ " + vet.getFullName() + " đã tạo lịch hẹn cho " + pet.getName() +
                        " vào " + req.getAppointmentTime());

        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public AppointmentResponse accept(Long appointmentId) {
        Appointment a = findAppointment(appointmentId);
        requirePending(a);
        a.setStatus(AppointmentStatus.ACCEPTED);
        appointmentRepository.save(a);

        notificationService.notify(a.getPetOwner(), "Lịch hẹn được xác nhận",
                "Bác sĩ " + a.getVeterinarian().getFullName() + " đã xác nhận lịch hẹn ngày " +
                        a.getAppointmentTime());
        return AppointmentResponse.from(a);
    }

    @Transactional
    public AppointmentResponse refuse(Long appointmentId, RefuseAppointmentRequest req) {
        Appointment a = findAppointment(appointmentId);
        requirePending(a);
        if (req.getReason() == null || req.getReason().isBlank()) {
            throw ApiException.badRequest("Vui lòng nhập lý do từ chối.");
        }
        a.setStatus(AppointmentStatus.REFUSED);
        a.setRefuseReason(req.getReason());
        appointmentRepository.save(a);

        notificationService.notify(a.getPetOwner(), "Lịch hẹn bị từ chối",
                "Bác sĩ " + a.getVeterinarian().getFullName() + " đã từ chối lịch hẹn. Lý do: " +
                        req.getReason());
        return AppointmentResponse.from(a);
    }

    /** Pet Owner hủy lịch hẹn - tối đa 3 lần/tháng theo acceptance criteria. */
    @Transactional
    public AppointmentResponse cancel(Long appointmentId, Long petOwnerId) {
        Appointment a = findAppointment(appointmentId);
        if (!a.getPetOwner().getId().equals(petOwnerId)) {
            throw ApiException.forbidden("Bạn không thể hủy lịch hẹn của người khác.");
        }
        if (a.getStatus() == AppointmentStatus.CANCELLED
                || a.getStatus() == AppointmentStatus.COMPLETED) {
            throw ApiException.badRequest("Lịch hẹn này không thể hủy.");
        }

        YearMonth thisMonth = YearMonth.now();
        LocalDateTime from = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime to = thisMonth.atEndOfMonth().atTime(23, 59, 59);
        long cancelledThisMonth = appointmentRepository
                .findByPetOwnerIdAndStatusAndCancelledAtBetween(
                        petOwnerId, AppointmentStatus.CANCELLED, from, to)
                .size();
        if (cancelledThisMonth >= MAX_CANCELLATIONS_PER_MONTH) {
            throw ApiException.badRequest(
                    "Bạn chỉ được hủy tối đa " + MAX_CANCELLATIONS_PER_MONTH + " lịch hẹn mỗi tháng.");
        }

        a.setStatus(AppointmentStatus.CANCELLED);
        a.setCancelledAt(LocalDateTime.now());
        appointmentRepository.save(a);

        notificationService.notify(a.getVeterinarian(), "Lịch hẹn bị hủy",
                a.getPetOwner().getFullName() + " đã hủy lịch hẹn ngày " + a.getAppointmentTime());
        return AppointmentResponse.from(a);
    }

    public List<AppointmentResponse> listForPetOwner(Long petOwnerId) {
        return appointmentRepository.findByPetOwnerId(petOwnerId).stream()
                .map(AppointmentResponse::from).toList();
    }

    public List<AppointmentResponse> listForVeterinarian(Long vetId) {
        return appointmentRepository.findByVeterinarianId(vetId).stream()
                .map(AppointmentResponse::from).toList();
    }

    /** User Story "View schedule": danh sách lịch đã ACCEPTED của veterinarian. */
    public List<AppointmentResponse> schedule(Long vetId) {
        return appointmentRepository
                .findByVeterinarianIdAndStatus(vetId, AppointmentStatus.ACCEPTED).stream()
                .map(AppointmentResponse::from).toList();
    }

    private void ensureNoTimeConflict(Long vetId, LocalDateTime time) {
        LocalDateTime from = time.minusMinutes(30);
        LocalDateTime to = time.plusMinutes(30);
        boolean conflict = !appointmentRepository
                .findByVeterinarianIdAndAppointmentTimeBetween(vetId, from, to).isEmpty();
        if (conflict) {
            throw ApiException.conflict("Bác sĩ đã có lịch hẹn khác trong khoảng thời gian này.");
        }
    }

    private void requirePending(Appointment a) {
        if (a.getStatus() != AppointmentStatus.PENDING) {
            throw ApiException.badRequest("Lịch hẹn này đã được xử lý trước đó.");
        }
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy lịch hẹn id=" + id));
    }

    private User getUser(Long id, Role expectedRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người dùng id=" + id));
        if (user.getRole() != expectedRole) {
            throw ApiException.badRequest("Người dùng id=" + id + " không có vai trò " + expectedRole);
        }
        return user;
    }
}
