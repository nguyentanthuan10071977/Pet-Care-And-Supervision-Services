package com.petcare.controller;

import com.petcare.dto.FacilityRequest;
import com.petcare.dto.FacilityResponse;
import com.petcare.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    /** User Story "View list facility" (Pet Owner). */
    @GetMapping
    public List<FacilityResponse> listAll() {
        return facilityService.listAll();
    }

    @GetMapping("/manager/{managerId}")
    public List<FacilityResponse> listByManager(@PathVariable Long managerId) {
        return facilityService.listByManager(managerId);
    }

    /** User Story "Create an facility" (Manager). */
    @PostMapping
    public ResponseEntity<FacilityResponse> create(@Valid @RequestBody FacilityRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.create(req));
    }

    /** User Story "Edit an facility" (Manager). */
    @PutMapping("/{id}")
    public FacilityResponse update(@PathVariable Long id, @Valid @RequestBody FacilityRequest req) {
        return facilityService.update(id, req);
    }

    /** User Story "Delete an facility" (Manager). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
