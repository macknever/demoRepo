package com.lawrence.springApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lawrence.springApp.model.Author;
import com.lawrence.springApp.service.MessageProducerService;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageProducerService messageProducerService;

    public MessageController(MessageProducerService messageProducerService) {
        this.messageProducerService = messageProducerService;
    }

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody Author author) {
        // If id is not provided, generate one
        if (author.getId() == null) {
            author.setId(java.util.UUID.randomUUID().toString());
        }
        messageProducerService.sendMessage(author);
        return new ResponseEntity<>("Notification sent to Kafka.", HttpStatus.CREATED);
    }
}
