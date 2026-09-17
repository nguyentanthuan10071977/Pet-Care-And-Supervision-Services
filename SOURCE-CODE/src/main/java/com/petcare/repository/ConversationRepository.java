package com.petcare.repository;

import com.petcare.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("select c from Conversation c join c.participants p where p.id = :userId")
    List<Conversation> findAllByParticipantId(Long userId);

    /** Chỉ lấy các hội thoại 1-1 (không phải group) mà userId tham gia; lọc người còn lại ở service. */
    @Query("select c from Conversation c join c.participants p " +
           "where c.isGroup = false and p.id = :userId")
    List<Conversation> findDirectConversationsForUser(Long userId);
}
