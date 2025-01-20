package com.example.everychat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    private String type;
    private String sender;
    private String channelId;
    private String message;
    private String ip;
    private LocalDateTime createAt;
}
