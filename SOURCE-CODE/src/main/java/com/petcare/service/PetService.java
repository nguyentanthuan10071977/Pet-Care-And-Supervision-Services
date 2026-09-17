package com.petcare.service;

import com.petcare.dto.*;
import com.petcare.entity.Device;
import com.petcare.entity.Pet;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.DeviceRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Story: View/Create/Edit/Delete pet profile, Add Device, Locate pet.
 */
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    public List<PetResponse> listByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId).stream().map(PetResponse::from).toList();
    }

    public PetResponse getById(Long petId) {
        return PetResponse.from(findPet(petId));
    }

    @Transactional
    public PetResponse create(PetRequest req) {
        User owner = userRepository.findById(req.getOwnerId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy pet owner."));

        // Acceptance criteria: input complete information in all fields.
        if (req.getName() == null || req.getName().isBlank()
                || req.getSpecies() == null || req.getSpecies().isBlank()
                || req.getBreed() == null || req.getBreed().isBlank()
                || req.getAge() == null) {
            throw ApiException.badRequest("Vui lòng nhập đầy đủ thông tin thú cưng.");
        }

        Pet pet = Pet.builder()
                .owner(owner)
                .name(req.getName())
                .species(req.getSpecies())
                .breed(req.getBreed())
                .age(req.getAge())
                .photo1Url(req.getPhoto1Url())
                .photo2Url(req.getPhoto2Url())
                .photo3Url(req.getPhoto3Url())
                .photo4Url(req.getPhoto4Url())
                .build();
        petRepository.save(pet);
        return PetResponse.from(pet);
    }

    @Transactional
    public PetResponse update(Long petId, PetRequest req) {
        Pet pet = findPet(petId);
        if (req.getName() == null || req.getName().isBlank()) {
            throw ApiException.badRequest("Không được để trống tất cả các trường.");
        }
        pet.setName(req.getName());
        pet.setSpecies(req.getSpecies());
        pet.setBreed(req.getBreed());
        pet.setAge(req.getAge());
        if (req.getPhoto1Url() != null) pet.setPhoto1Url(req.getPhoto1Url());
        if (req.getPhoto2Url() != null) pet.setPhoto2Url(req.getPhoto2Url());
        if (req.getPhoto3Url() != null) pet.setPhoto3Url(req.getPhoto3Url());
        if (req.getPhoto4Url() != null) pet.setPhoto4Url(req.getPhoto4Url());
        petRepository.save(pet);
        return PetResponse.from(pet);
    }

    @Transactional
    public void delete(Long petId) {
        petRepository.delete(findPet(petId));
    }

    public List<PetResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw ApiException.badRequest("Vui lòng nhập ít nhất 1 ký tự để tìm kiếm.");
        }
        return petRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(PetResponse::from).toList();
    }

    // ----- User Story: Add Device / Locate pet (GPS) -----

    @Transactional
    public LocationResponse addDevice(Long petId, DeviceRequest req) {
        Pet pet = findPet(petId);
        if (pet.getDevice() != null) {
            throw ApiException.conflict("Thú cưng này đã có thiết bị định vị.");
        }
        Device device = Device.builder()
                .pet(pet)
                .deviceCode(req.getDeviceCode())
                .build();
        deviceRepository.save(device);
        pet.setDevice(device);
        petRepository.save(pet);
        return toLocationResponse(device);
    }

    @Transactional
    public LocationResponse updateLocation(Long petId, LocationUpdateRequest req) {
        Device device = deviceRepository.findByPetId(petId)
                .orElseThrow(() -> ApiException.notFound(
                        "Thú cưng chưa gắn thiết bị định vị GPS."));
        device.setLatitude(req.getLatitude());
        device.setLongitude(req.getLongitude());
        device.setLastUpdated(LocalDateTime.now());
        deviceRepository.save(device);
        return toLocationResponse(device);
    }

    public LocationResponse locate(Long petId) {
        Device device = deviceRepository.findByPetId(petId)
                .orElseThrow(() -> ApiException.notFound(
                        "Thú cưng chưa gắn thiết bị định vị GPS."));
        return toLocationResponse(device);
    }

    private LocationResponse toLocationResponse(Device device) {
        return new LocationResponse(device.getDeviceCode(), device.getLatitude(),
                device.getLongitude(), device.getLastUpdated());
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy thú cưng id=" + petId));
    }
}
