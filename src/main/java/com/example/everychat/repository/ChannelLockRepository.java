package com.example.everychat.repository;

import com.example.everychat.domain.ChannelLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelLockRepository extends JpaRepository<ChannelLock, String> {
}
