package com.example.everychat.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelLock {

    @Id
    private String channelId;
    private String lockPw;

}
