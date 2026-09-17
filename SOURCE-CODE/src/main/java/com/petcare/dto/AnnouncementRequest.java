package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementRequest {
    @NotNull
    private Long managerId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
