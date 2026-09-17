package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String deviceCode;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdated;
}
