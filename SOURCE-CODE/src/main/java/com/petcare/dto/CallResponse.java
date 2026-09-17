package com.petcare.dto;

import com.petcare.entity.CallSession;
import com.petcare.entity.CallStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallResponse {
    private Long id;
    private Long callerId;
    private Long calleeId;
    private CallStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static CallResponse from(CallSession c) {
        return new CallResponse(c.getId(), c.getCaller().getId(), c.getCallee().getId(),
                c.getStatus(), c.getStartedAt(), c.getEndedAt());
    }
}
