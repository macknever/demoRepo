package com.lawrence.greenmail.util;

import java.time.Duration;
import java.util.List;

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
    void receivesEmailWithExpectedBodyAndAttachments() throws Exception {
        // Set these to the report you send before running this mailbox validation test.
        String subject = "Alert: try to submit issue with a 20mb file";
        String expectedBody = "Category";
        List<String> expectedAttachments = List.of("maxSize20mb.tiff");
        try (var client = clientProvider.get()) {
            new MailValidator(client).validate(subject, expectedBody, expectedAttachments, Duration.ofSeconds(60));
        }
    }

}
