package com.petcare.controller;

import com.petcare.dto.*;
import com.petcare.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /** User Story "View list pets". */
    @GetMapping
    public List<PetResponse> listByOwner(@RequestParam Long ownerId) {
        return petService.listByOwner(ownerId);
    }

    @GetMapping("/{id}")
    public PetResponse getById(@PathVariable Long id) {
        return petService.getById(id);
    }

    /** User Story "Search Veterinarian or pets" - phần pet. */
    @GetMapping("/search")
    public List<PetResponse> search(@RequestParam String q) {
        return petService.search(q);
    }

    /** User Story "Create pet profile". */
    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.create(req));
    }

    /** User Story "Edit pet profile". */
    @PutMapping("/{id}")
    public PetResponse update(@PathVariable Long id, @Valid @RequestBody PetRequest req) {
        return petService.update(id, req);
    }

    /** User Story "Delete pet profile". */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** User Story "Add Device". */
    @PostMapping("/{id}/device")
    public LocationResponse addDevice(@PathVariable Long id, @Valid @RequestBody DeviceRequest req) {
        return petService.addDevice(id, req);
    }

    /** Thiết bị GPS gửi cập nhật tọa độ (được gọi bởi thiết bị định vị thực tế). */
    @PutMapping("/{id}/location")
    public LocationResponse updateLocation(@PathVariable Long id,
                                            @Valid @RequestBody LocationUpdateRequest req) {
        return petService.updateLocation(id, req);
    }

    /** User Story "Locate pet". */
    @GetMapping("/{id}/location")
    public LocationResponse locate(@PathVariable Long id) {
        return petService.locate(id);
    }
}
