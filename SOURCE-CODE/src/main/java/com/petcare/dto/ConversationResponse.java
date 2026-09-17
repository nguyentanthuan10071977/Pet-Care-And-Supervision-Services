package com.petcare.dto;

import com.petcare.entity.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private String title;
    private Boolean isGroup;
    private List<UserResponse> participants;

    public static ConversationResponse from(Conversation c) {
        return new ConversationResponse(c.getId(), c.getTitle(), c.getIsGroup(),
                c.getParticipants().stream().map(UserResponse::from).collect(Collectors.toList()));
    }
}
