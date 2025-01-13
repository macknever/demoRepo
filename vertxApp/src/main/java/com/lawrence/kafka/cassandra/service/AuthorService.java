package com.lawrence.kafka.cassandra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.lawrence.kafka.entity.Author;
import com.lawrence.kafka.cassandra.repository.AuthorRepository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

public class AuthorService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);
    private final AuthorRepository authorRepository;

    @Inject
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Future<Void> initRepository() {
        return this.authorRepository.initRepository();
    }

    public Future<Void> addAuthor(Author author) {
        
        return authorRepository.insert(author)
                .onSuccess(v -> LOG.info("Successfully added author {}", author))
                .onFailure(e -> {
                    LOG.error("Failed to add author", e);
                });
    }
}
