package com.lawrence.greenmail.util;

import java.time.Duration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lawrence.greenmail.client.ImapMailClient;
import com.lawrence.greenmail.guice.module.ImapMailModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Run explicitly with mailbox credentials and the subject of a report already sent. */
class MailValidatorIT {
    @Inject
    private Provider<ImapMailClient> clientProvider;

    @BeforeEach
    void setUp() {
        Guice.createInjector(new ImapMailModule()).injectMembers(this);
    }

    @Test
    void receivesEmailWithExpectedAttachment() throws Exception {
        String subject = requiredEnvironment("SMOKE_IMAP_SUBJECT");
        String attachmentName = requiredEnvironment("SMOKE_IMAP_ATTACHMENT");
        try (var client = clientProvider.get()) {
            new MailValidator(client).validate(subject, attachmentName, Duration.ofSeconds(60));
        }
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Set " + name + " before running this test");
        }
        return value;
    }
}
