package com.lawrence.kafka.util;

import com.lawrence.kafka.entity.Author;

import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

public class AuthorUtil {
    private AuthorUtil() {
        throw new AssertionError("Utility class");
    }

    public static Author generateAuthor(KafkaConsumerRecord<String, String> record) {

        JsonObject authorJson = new JsonObject(record.value());
        String authorId = authorJson.getString("id");
        String authorName = authorJson.getString("name");
        String authorPersonalName = authorJson.getString("personalName");

        return new Author(authorId, authorName, authorPersonalName);
    }
}
