package com.lawrence.springApp.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.lawrence.springApp.model.Author;
import com.lawrence.springApp.model.AuthorRepository;

@Service
public class MessageConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerService.class);
    private final AuthorRepository authorRepository;

    public MessageConsumerService(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

//    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
//    public void listenMessages(ConsumerRecord<String, String> record) {
//
//        authorRepository.save(author);
//        LOG.info("Messeage stored, {}", author);
//    }
//
//    private Author convertToAuthor(ConsumerRecord<String, String> record) {
//        record.value().get
//    }
}
