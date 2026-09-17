package com.petcare.service;

import com.petcare.dto.*;
import com.petcare.entity.ChatMessage;
import com.petcare.entity.Conversation;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.ChatMessageRepository;
import com.petcare.repository.ConversationRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Story: Chat with Veterinarian (1-1 hoặc group).
 * Triển khai qua REST đơn giản (gửi/nhận tin nhắn); có thể nâng cấp lên
 * WebSocket/STOMP để nhận tin nhắn real-time - xem README.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<ConversationResponse> listForUser(Long userId) {
        return conversationRepository.findAllByParticipantId(userId).stream()
                .map(ConversationResponse::from).toList();
    }

    /** Lấy (hoặc tạo mới) hội thoại 1-1 / group giữa các participantIds. */
    @Transactional
    public ConversationResponse getOrCreateConversation(CreateConversationRequest req) {
        List<Long> ids = req.getParticipantIds();
        if (ids.size() < 2) {
            throw ApiException.badRequest("Cần ít nhất 2 người tham gia hội thoại.");
        }

        if (ids.size() == 2) {
            Long otherId = ids.get(1);
            var existing = conversationRepository.findDirectConversationsForUser(ids.get(0)).stream()
                    .filter(c -> c.getParticipants().size() == 2)
                    .filter(c -> c.getParticipants().stream().anyMatch(p -> p.getId().equals(otherId)))
                    .findFirst();
            if (existing.isPresent()) {
                return ConversationResponse.from(existing.get());
            }
        }

        Set<User> participants = new HashSet<>();
        for (Long id : ids) {
            participants.add(userRepository.findById(id)
                    .orElseThrow(() -> ApiException.notFound("Không tìm thấy người dùng id=" + id)));
        }

        Conversation conversation = Conversation.builder()
                .participants(participants)
                .isGroup(ids.size() > 2)
                .title(ids.size() > 2 ? req.getTitle() : null)
                .build();
        conversationRepository.save(conversation);
        return ConversationResponse.from(conversation);
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long conversationId, ChatMessageRequest req) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy hội thoại."));
        User sender = userRepository.findById(req.getSenderId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người gửi."));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(sender.getId()));
        if (!isParticipant) {
            throw ApiException.forbidden("Bạn không thuộc hội thoại này.");
        }

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .content(req.getContent())
                .build();
        chatMessageRepository.save(message);

        conversation.getParticipants().stream()
                .filter(p -> !p.getId().equals(sender.getId()))
                .forEach(p -> notificationService.notify(p, "Tin nhắn mới từ " + sender.getFullName(),
                        req.getContent()));

        return ChatMessageResponse.from(message);
    }

    public List<ChatMessageResponse> listMessages(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(ChatMessageResponse::from).toList();
    }
}
