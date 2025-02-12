package com.example.everychat.repository;

import com.example.everychat.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findAllByChannelIdOrderByCreateAtDesc(String channelId, Pageable pageable);

    @Query("""
SELECT EXISTS (
    SELECT 1
    FROM Message m
    WHERE m.createAt >= TIMESTAMPADD(MINUTE, -20, CURRENT_TIMESTAMP)
    AND m.channelId = :channelId
)
""")
    boolean existsRecentChat(String channelId);

}
