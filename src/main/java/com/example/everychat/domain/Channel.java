package com.example.everychat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "delete_at IS NULL")
public class Channel {
    @Id
    private String id;
    private String channelName;
    private String ip;
    private String pw;
    private Boolean isLock;
    private Long categoryId;
    private LocalDateTime createAt;
    private LocalDateTime deleteAt;

    @Transient
    private Integer memberCount;

    @Transient
    private Boolean hasNewChat;
}
