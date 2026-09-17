package com.petcare.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateConversationRequest {
    @NotEmpty
    private List<Long> participantIds;
    private String title; // bắt buộc nếu group (participantIds.size() > 2)
}
