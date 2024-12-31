package com.example.everychat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    private String id;
    private String channelName;
    private String ip;
    private String pw;
    private Boolean isLock;
    private LocalDateTime createAt;
    private LocalDateTime deleteAt;
}
