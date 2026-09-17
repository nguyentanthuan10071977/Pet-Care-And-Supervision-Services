package com.petcare.service;

import com.petcare.dto.FacilityRequest;
import com.petcare.dto.FacilityResponse;
import com.petcare.entity.Facility;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.FacilityRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** User Story: Manage/Create/Edit/Delete facility (Manager), View list facility (Pet Owner). */
@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    public List<FacilityResponse> listAll() {
        return facilityRepository.findAll().stream().map(FacilityResponse::from).toList();
    }

    public List<FacilityResponse> listByManager(Long managerId) {
        return facilityRepository.findByManagerId(managerId).stream()
                .map(FacilityResponse::from).toList();
    }

    @Transactional
    public FacilityResponse create(FacilityRequest req) {
        User manager = userRepository.findById(req.getManagerId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy manager."));
        if (manager.getRole() != Role.MANAGER) {
            throw ApiException.badRequest("Chỉ Manager mới có thể tạo facility.");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw ApiException.badRequest("Không được để trống tên facility.");
        }
        Facility facility = Facility.builder()
                .manager(manager)
                .name(req.getName())
                .address(req.getAddress())
                .description(req.getDescription())
                .build();
        facilityRepository.save(facility);
        return FacilityResponse.from(facility);
    }

    @Transactional
    public FacilityResponse update(Long facilityId, FacilityRequest req) {
        Facility facility = findFacility(facilityId);
        if (req.getName() == null || req.getName().isBlank()) {
            throw ApiException.badRequest("Không được để trống tất cả các trường.");
        }
        facility.setName(req.getName());
        facility.setAddress(req.getAddress());
        facility.setDescription(req.getDescription());
        facilityRepository.save(facility);
        return FacilityResponse.from(facility);
    }

    @Transactional
    public void delete(Long facilityId) {
        facilityRepository.delete(findFacility(facilityId));
    }

    private Facility findFacility(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy facility id=" + id));
    }
}
