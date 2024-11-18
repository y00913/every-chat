package com.example.everychat.service;

import com.example.everychat.domain.Channel;
import com.example.everychat.domain.ChannelLock;
import com.example.everychat.domain.Message;
import com.example.everychat.dto.MessageDto;
import com.example.everychat.dto.PagingChannelDto;
import com.example.everychat.dto.PagingMessageDto;
import com.example.everychat.enums.MessageTypeEnum;
import com.example.everychat.repository.ChannelLockRepository;
import com.example.everychat.repository.ChannelRepository;
import com.example.everychat.repository.MessageRepository;
import com.example.everychat.util.AesUtil;
import com.example.everychat.vo.ChannelVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ChannelLockRepository channelLockRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final HttpServletRequest request;
    private static HashMap<String, Integer> roomCount = new HashMap<>();

    @Transactional(readOnly = true)
    @Override
    public Object getChannelList(int page) {
        Page<Channel> channelPage = channelRepository.findAllByOrderByCreateAtDesc(PageRequest.of(page, 10));
        PagingChannelDto pagingChannelDto = PagingChannelDto.builder()
                .channelList(channelPage.getContent())
                .pageNumber(channelPage.getNumber())
                .pageSize(channelPage.getTotalPages())
                .build();

        return pagingChannelDto;
    }

    @Transactional(readOnly = true)
    @Override
    public Object getChannelListByName(String searchName, int page) {
        Page<Channel> channelPage = channelRepository.findAllByChannelNameContainsOrderByCreateAtDesc(searchName, PageRequest.of(page, 10));
        PagingChannelDto pagingChannelDto = PagingChannelDto.builder()
                .channelList(channelPage.getContent())
                .pageNumber(channelPage.getNumber())
                .pageSize(channelPage.getTotalPages())
                .build();

        return pagingChannelDto;
    }

    @Transactional
    @Override
    public Object createChannel(ChannelVo channelVo) throws Exception {
        Channel channel = Channel.builder().id(UUID.randomUUID().toString())
                .channelName(channelVo.getChannelName())
                .ip(channelVo.getIp())
                .pw(AesUtil.encrypt(channelVo.getPw()))
                .isLock(channelVo.getIsLock())
                .createAt(LocalDateTime.now())
                .build();

        channelRepository.save(channel);

        if(channel.getIsLock()){
            ChannelLock channelLock = ChannelLock.builder()
                    .channelId(channel.getId())
                    .lockPw(AesUtil.encrypt(channelVo.getLockPw()))
                    .build();

            channelLockRepository.save(channelLock);
        }

        log.info("채널 생성 : " + channel.getId());
        log.info(channel.toString());

        return channel;
    }

    @Transactional
    @Override
    public void sendMessage(MessageDto messageDto) {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(messageDto.getChannelId())
                .type(messageDto.getType())
                .sender(messageDto.getSender())
                .message(messageDto.getMessage())
                .ip(messageDto.getIp())
                .createAt(LocalDateTime.now())
                .build();

        if(messageDto.getType().equals(MessageTypeEnum.MESSAGE.getLabel())) {
            messageRepository.save(message);
        } else if(messageDto.getType().equals(MessageTypeEnum.ENTER.getLabel())) {
            System.out.println("channelId : " + messageDto.getChannelId());
            System.out.println("enter " + messageDto.getSender());
        } else {
            System.out.println("channelId : " + messageDto.getChannelId());
            System.out.println("leave " + messageDto.getSender());
        }

        message.setIp(messageDto.getIp().substring(0, messageDto.getIp().indexOf('.', 5)));

        simpMessageSendingOperations.convertAndSend("/topic/" + message.getChannelId(), message);
    }

    @Transactional(readOnly = true)
    @Override
    public Object getMessagePaging(String channelId, int page){
        Page<Message> messagePage = messageRepository.findAllByChannelIdOrderByCreateAtDesc(channelId, PageRequest.of(page, 10));
        messagePage.stream().forEach(message -> message.setIp(message.getIp().substring(0, message.getIp().indexOf('.', 5))));
        PagingMessageDto pagingMessageDto = PagingMessageDto.builder()
                .messageList(messagePage.getContent().stream().sorted(Comparator.comparing(Message::getCreateAt)).collect(Collectors.toList()))
                .pageNumber(messagePage.getNumber())
                .pageSize(messagePage.getTotalPages())
                .build();
        return pagingMessageDto;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkLockPw(String channelId, String lockPw) throws Exception {
        ChannelLock channelLock = channelLockRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channelLock doesn't exist"));

        if(lockPw.equals(AesUtil.decrypt(channelLock.getLockPw()))) {
            HttpSession session = request.getSession();

            String attributeName = "channelLockSuccess_" + channelId;
            session.setAttribute(attributeName, true);
            log.info("attributeName : {}", attributeName);
            log.info(session.getAttribute("channelLockSuccess_" + channelId).toString());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkLockVerify(String channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channel doesn't exist"));

        if(!channel.getIsLock()) return true;

        HttpSession session = request.getSession();

        log.info("channelId : {}", channelId);
        String attributeName = "channelLockSuccess_" + channelId;
        Boolean isPasswordChecked = (Boolean) session.getAttribute(attributeName);
        log.info("password : {}",isPasswordChecked);

        if (isPasswordChecked != null && isPasswordChecked) {
            session.setAttribute(attributeName, "");

            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean deleteRoom(String channelId, String pw) throws Exception {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channel doesn't exist"));

        if(pw.equals(AesUtil.decrypt(channel.getPw()))) {
            if(channel.getIsLock()) {
                channelLockRepository.deleteById(channelId);
            }
            channelRepository.deleteById(channelId);

            log.info("채널 삭제 : " + channelId);
            return true;
        } else {
            log.info("채널 삭제 실패");
            return false;
        }
    }

    @Override
    public void addCount(String channelId) {
        int next = 1;
        if(roomCount.containsKey(channelId)) {
            next = roomCount.get(channelId) + 1;
            roomCount.replace(channelId, next);
        } else {
            roomCount.put(channelId, next);
        }
    }

    @Override
    public void subtractCount(String channelId) {
        int next = roomCount.get(channelId) - 1;
        roomCount.replace(channelId, next);
    }

    @Override
    public void sendCount(String channelId) throws InterruptedException {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(channelId)
                .type("count")
                .message(roomCount.get(channelId).toString())
                .createAt(LocalDateTime.now())
                .build();
        Thread.sleep(100);
        simpMessageSendingOperations.convertAndSend("/topic/" + channelId, message);
    }

    @Transactional(readOnly = true)
    public boolean checkExistName(String name) {
        return channelRepository.existsByChannelName(name);
    }

}
