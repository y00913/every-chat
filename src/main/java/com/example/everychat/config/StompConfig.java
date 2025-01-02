package com.example.everychat.config;

import com.example.everychat.interceptor.ClientIpHandshakeInterceptor;
import com.example.everychat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${host}")
    private String Host;
    @Value("${relayPort}")
    private int relayPort;
    @Value("${clientId}")
    private String clientId;
    @Value("${clientPw}")
    private String clientPw;
    private final ChatService chatService;
    private static HashMap<String, String> sessions = new HashMap<>();

    public StompConfig(@Lazy ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/pub")
                .enableStompBrokerRelay("/topic")
                .setRelayHost(Host)
                .setVirtualHost("/")
                .setRelayPort(relayPort)
                .setClientLogin(clientId)
                .setClientPasscode(clientPw);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("https://everychat.kro.kr")
//                .setAllowedOriginPatterns("http://localhost:3000")
                .addInterceptors(new ClientIpHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("message inbound : {}", message);
                return message;
            }

            @SneakyThrows
            @Override
            public void postSend(Message message, MessageChannel channel, boolean sent) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
                    String channelPath = (String) message.getHeaders().get("simpDestination");
                    String channelId = channelPath.substring(channelPath.lastIndexOf("/")+1);

                    if(channelId != null) {
                        chatService.addCount(channelId);
                        chatService.sendCount(channelId);
                        sessions.put(accessor.getSessionId(), channelId);
                    }

                } else if(StompCommand.DISCONNECT.equals(accessor.getCommand()) || StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
                    String channelId = sessions.get(message.getHeaders().get("simpSessionId"));

                    if(channelId != null) {
                        chatService.subtractCount(channelId);
                        chatService.sendCount(channelId);
                        sessions.remove(accessor.getSessionId());
                    }
                }
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("message outbound : {}", message);
                return message;
            }
        });
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(Host);
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setEncoding("utf8");
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.registerModule(dateTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        return converter;
    }

    @Bean
    public JavaTimeModule dateTimeModule(){
        return new JavaTimeModule();
    }

}
