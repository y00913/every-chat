package com.example.everychat.service;

import com.example.everychat.domain.Channel;
import com.example.everychat.domain.Message;
import com.example.everychat.dto.MessageDto;
import com.example.everychat.repository.ChannelRepository;
import com.example.everychat.repository.MessageRepository;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @Transactional(readOnly = true)
    @Override
    public Object getChannelList() {
        List<Channel> channelList = channelRepository.findAllByOrderByCreateAtDesc();

        return channelList;
    }

    @Transactional
    @Override
    public void createChannel(ChannelVo channelVo) {
        Channel channel = Channel.builder().id(UUID.randomUUID().toString())
                .channelName(channelVo.getChannelName())
                .createAt(LocalDateTime.now())
                .build();

        channelRepository.save(channel);
    }

    @Transactional
    @Override
    public void sendMessage(MessageDto messageDto) {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(messageDto.getChannelId())
                .type(messageDto.getType())
                .sender(messageDto.getSender())
                .createAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        simpMessageSendingOperations.convertAndSend("/topic/" + message.getChannelId(), message);
    }
}
