package com.lawrence.springApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.lawrence.springApp.model.Author;

@Service
public class MessageProducerService {
    private final KafkaTemplate<String, Author> kafkaTemplate;
    private final String topic;

    public MessageProducerService(KafkaTemplate<String, Author> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(Author author) {
        kafkaTemplate.send(topic, author.getId(), author);
    }
}
