package com.example.everychat.repository;

import com.example.everychat.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {

    Page<Message> findAllByChannelIdOrderByCreateAtDesc(String channelId, Pageable pageable);

}
