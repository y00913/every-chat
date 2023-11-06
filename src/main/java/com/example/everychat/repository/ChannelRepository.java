package com.example.everychat.repository;

import com.example.everychat.domain.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, String> {

    Page<Channel> findAllByOrderByCreateAtDesc(Pageable pageable);
    Page<Channel> findAllByChannelNameContainsOrderByCreateAtDesc(String searchName, Pageable pageable);

}
