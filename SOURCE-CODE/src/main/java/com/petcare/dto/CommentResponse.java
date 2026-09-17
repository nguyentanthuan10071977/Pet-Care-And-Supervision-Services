package com.petcare.dto;

import com.petcare.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getAuthor().getId(), c.getAuthor().getFullName(),
                c.getContent(), c.getCreatedAt());
    }
}
