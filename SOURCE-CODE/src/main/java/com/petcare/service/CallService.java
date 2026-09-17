package com.petcare.service;

import com.petcare.dto.CallRequest;
import com.petcare.dto.CallResponse;
import com.petcare.entity.CallSession;
import com.petcare.entity.CallStatus;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.CallSessionRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * User Story: Video call with Veterinarian.
 * Đây là tầng "signaling" quản lý trạng thái cuộc gọi (ringing/ongoing/ended).
 * Truyền video/audio thực tế cần WebRTC ở client, xem README.
 */
@Service
@RequiredArgsConstructor
public class CallService {

    private final CallSessionRepository callSessionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CallResponse start(CallRequest req) {
        User caller = userRepository.findById(req.getCallerId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người gọi."));
        User callee = userRepository.findById(req.getCalleeId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người được gọi."));

        CallSession session = CallSession.builder().caller(caller).callee(callee).build();
        callSessionRepository.save(session);

        notificationService.notify(callee, "Cuộc gọi video đến",
                caller.getFullName() + " đang gọi video cho bạn.");
        return CallResponse.from(session);
    }

    @Transactional
    public CallResponse accept(Long callId) {
        CallSession session = findSession(callId);
        session.setStatus(CallStatus.ONGOING);
        callSessionRepository.save(session);
        return CallResponse.from(session);
    }

    @Transactional
    public CallResponse end(Long callId) {
        CallSession session = findSession(callId);
        if (session.getStatus() == CallStatus.RINGING) {
            session.setStatus(CallStatus.MISSED);
        } else {
            session.setStatus(CallStatus.ENDED);
        }
        session.setEndedAt(LocalDateTime.now());
        callSessionRepository.save(session);
        return CallResponse.from(session);
    }

    private CallSession findSession(Long id) {
        return callSessionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy cuộc gọi id=" + id));
    }
}
