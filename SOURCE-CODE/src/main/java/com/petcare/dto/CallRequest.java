package com.petcare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CallRequest {
    @NotNull
    private Long callerId;
    @NotNull
    private Long calleeId;
}
