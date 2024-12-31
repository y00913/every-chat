package com.example.everychat.dto;

import com.example.everychat.annotation.ClientIp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String type;
    private String sender;
    private String channelId;
    private String message;
    @ClientIp
    private String ip;
}
