package com.petcare.service;

import com.petcare.dto.AnnouncementRequest;
import com.petcare.dto.AnnouncementResponse;
import com.petcare.dto.StatisticsResponse;
import com.petcare.entity.*;
import com.petcare.exception.ApiException;
import com.petcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** User Story: Posts announcements, View/Export statistical (Manager). */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final FacilityRepository facilityRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest req) {
        User manager = userRepository.findById(req.getManagerId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy manager."));
        if (manager.getRole() != Role.MANAGER) {
            throw ApiException.badRequest("Chỉ Manager mới có thể đăng thông báo.");
        }
        Announcement announcement = Announcement.builder()
                .manager(manager)
                .title(req.getTitle())
                .content(req.getContent())
                .build();
        announcementRepository.save(announcement);
        return AnnouncementResponse.from(announcement);
    }

    public List<AnnouncementResponse> listAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(AnnouncementResponse::from).toList();
    }

    /** User Story "View Statistical". */
    public StatisticsResponse statistics() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return new StatisticsResponse(
                userRepository.findByRole(Role.PET_OWNER).size(),
                userRepository.findByRole(Role.VETERINARIAN).size(),
                petRepository.findAll().size(),
                facilityRepository.findAll().size(),
                appointments.size(),
                appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count(),
                appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.ACCEPTED).count(),
                appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count()
        );
    }

    /** User Story "Export Statistical": xuất dữ liệu thống kê dạng CSV. */
    public String exportStatisticsCsv() {
        StatisticsResponse s = statistics();
        StringBuilder sb = new StringBuilder();
        sb.append("Chỉ số,Giá trị\n");
        sb.append("Tổng số Pet Owner,").append(s.getTotalPetOwners()).append("\n");
        sb.append("Tổng số Veterinarian,").append(s.getTotalVeterinarians()).append("\n");
        sb.append("Tổng số thú cưng,").append(s.getTotalPets()).append("\n");
        sb.append("Tổng số facility,").append(s.getTotalFacilities()).append("\n");
        sb.append("Tổng số lịch hẹn,").append(s.getTotalAppointments()).append("\n");
        sb.append("Lịch hẹn đang chờ,").append(s.getPendingAppointments()).append("\n");
        sb.append("Lịch hẹn đã xác nhận,").append(s.getAcceptedAppointments()).append("\n");
        sb.append("Lịch hẹn đã hủy,").append(s.getCancelledAppointments()).append("\n");
        return sb.toString();
    }
}
