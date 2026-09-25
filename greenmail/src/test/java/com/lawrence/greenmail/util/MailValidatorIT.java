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
        String subject = "Smoke report 8f8ca5a1-63b0-44c4-a21d-0cf330e900be";
        String expectedBody = "The smoke test report is attached.";
        List<String> expectedAttachments = List.of("report.zip", "details.txt");
        try (var client = clientProvider.get()) {
            new MailValidator(client).validate(subject, expectedBody, expectedAttachments, Duration.ofSeconds(60));
        }
    }

}
