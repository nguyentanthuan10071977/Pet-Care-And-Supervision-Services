package com.petcare.dto;

import com.petcare.entity.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityResponse {
    private Long id;
    private String name;
    private String address;
    private String description;
    private Long managerId;

    public static FacilityResponse from(Facility f) {
        return new FacilityResponse(f.getId(), f.getName(), f.getAddress(), f.getDescription(),
                f.getManager().getId());
    }
}
