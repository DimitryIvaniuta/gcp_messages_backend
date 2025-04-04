package com.messages.engine.repository;

import com.messages.engine.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Conversation entities.
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
