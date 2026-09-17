package com.petcare.controller;

import com.petcare.dto.*;
import com.petcare.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** User Story: Chat with Veterinarian (1-1 / group). */
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    public List<ConversationResponse> listForUser(@RequestParam Long userId) {
        return chatService.listForUser(userId);
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> getOrCreateConversation(
            @Valid @RequestBody CreateConversationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.getOrCreateConversation(req));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(@PathVariable Long conversationId,
                                                            @Valid @RequestBody ChatMessageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.sendMessage(conversationId, req));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<ChatMessageResponse> listMessages(@PathVariable Long conversationId) {
        return chatService.listMessages(conversationId);
    }
}
