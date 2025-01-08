package com.example.everychat.dto;

import com.example.everychat.annotation.ClientIp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto {
    private String channelName;
    private String pw;
    @ClientIp
    private String ip;
    private Boolean isLock;
    private String lockPw;
}
