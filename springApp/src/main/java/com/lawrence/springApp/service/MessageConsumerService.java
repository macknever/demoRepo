package com.lawrence.springApp.service;

import com.lawrence.springApp.model.AuthorRepository;

public class MessageConsumerService {
    private final AuthorRepository authorRepository;

    public MessageConsumerService(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
}
