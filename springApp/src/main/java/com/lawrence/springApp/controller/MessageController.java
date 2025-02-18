package com.lawrence.springApp.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lawrence.springApp.model.Author;
import com.lawrence.springApp.service.MessageConsumerService;
import com.lawrence.springApp.service.MessageProducerService;

@RestController
@RequestMapping("/api")
public class MessageController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);


    @Value("${app.kafka.topic}")
    String allowedTopic;

    private final MessageProducerService messageProducerService;

    public MessageController(MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
    }

    @PostMapping(value = "/messages/{topic}")
    public ResponseEntity<String> createNotification(@RequestBody Author author, @PathVariable String topic) {

        if (topic == null || !topic.equals(allowedTopic)) {
            return new ResponseEntity<>("Invalid topic.", HttpStatus.NOT_FOUND);
        }
        LOG.info("Message received {} at {}", author, new Date().toInstant());

        messageProducerService.sendMessage(author);
        return new ResponseEntity<>("Notification sent to Kafka.", HttpStatus.CREATED);
    }
}
