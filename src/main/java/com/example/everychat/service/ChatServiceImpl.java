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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Object getChannelList(int page) {
        Page<Channel> channelList = channelRepository.findAllByOrderByCreateAtDesc(PageRequest.of(page, 10));

        return channelList;
    }

    @Transactional
    @Override
    public void createChannel(ChannelVo channelVo) {
        Channel channel = Channel.builder().id(UUID.randomUUID().toString())
                .channelName(channelVo.getChannelName())
                .createAt(LocalDateTime.now())
                .build();

        log.info(channel.toString());

        channelRepository.save(channel);
    }

    @Transactional
    @Override
    public void sendMessage(MessageDto messageDto) {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(messageDto.getChannelId())
                .type(messageDto.getType())
                .sender(messageDto.getSender())
                .message(messageDto.getMessage())
                .createAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        simpMessageSendingOperations.convertAndSend("/topic/" + message.getChannelId(), message);
    }

    @Transactional
    @Override
    public Object getMessagePaging(int page){
        Page<Message> messagePage = messageRepository.findAllByOrderByCreateAtDesc(PageRequest.of(page, 10));
        List<Message> messageList = messagePage.getContent().stream().sorted(Comparator.comparing(Message::getCreateAt)).collect(Collectors.toList());
        return messageList;
    }
}
