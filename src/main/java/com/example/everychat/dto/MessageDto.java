package com.example.everychat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String type;
    private String sender;
    private String channelId;
    private String message;
    private String ip;
    private String uuid;
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
}