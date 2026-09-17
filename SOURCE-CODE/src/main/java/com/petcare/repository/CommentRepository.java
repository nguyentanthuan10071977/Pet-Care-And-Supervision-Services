package com.petcare.repository;

import com.petcare.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVeterinarianIdOrderByCreatedAtDesc(Long veterinarianId);
}
