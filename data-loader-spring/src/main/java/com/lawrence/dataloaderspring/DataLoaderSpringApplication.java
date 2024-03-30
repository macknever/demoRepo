package com.lawrence.dataloaderspring;

import com.lawrence.dataloaderspring.model.Author;
import com.lawrence.dataloaderspring.model.AuthorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataLoaderSpringApplication {

    @Autowired
    AuthorRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderSpringApplication.class, args);
    }

    @PostConstruct
    public void save() {
        Author author = new Author();
        author.setId("newId");
        author.setName("Lawrence");
        author.setPersonalName("Lawrence.Li");
        repository.save(author);
    }

}
