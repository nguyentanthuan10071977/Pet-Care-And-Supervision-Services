package com.petcare.service;

import com.petcare.dto.NotificationResponse;
import com.petcare.entity.Notification;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** User Story: View notifications / Delete a notification. */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> listForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional
    public void delete(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy thông báo."));
        if (!n.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("Bạn không thể xoá thông báo của người khác.");
        }
        notificationRepository.delete(n);
    }

    /** Dùng nội bộ bởi các service khác để tạo thông báo (appointment, chat, ...). */
    @Transactional
    public Notification notify(User user, String title, String content) {
        Notification n = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .build();
        return notificationRepository.save(n);
    }
}
