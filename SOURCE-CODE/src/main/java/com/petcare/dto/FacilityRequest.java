package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FacilityRequest {
    @NotNull
    private Long managerId;
    @NotBlank
    private String name;
    private String address;
    private String description;
}
