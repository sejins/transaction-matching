package com.jingeore.chatting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, Long> {
}
