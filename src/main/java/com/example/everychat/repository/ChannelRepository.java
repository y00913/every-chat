package com.example.everychat.repository;

import com.example.everychat.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, String> {

    List<Channel> findAllByOrderByCreateAtDesc();

}
