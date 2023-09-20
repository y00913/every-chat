package com.example.everychat.config;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StompListener {

    private static final Logger log = LoggerFactory.getLogger(StompListener.class);

    @RabbitListener(queues = "sample.queue")
    public void receiveMessage(final Message message){
        log.info(message.toString());
    }
}
