package com.lawrence.greenmail.util;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.lawrence.greenmail.client.ImapMailClient;
import com.lawrence.greenmail.client.ImapMailClient.EmailContent;
import jakarta.mail.MessagingException;

/** Validates delivery; the caller owns and closes the injected client. */
public final class MailValidator {
    private static final Logger LOG = LoggerFactory.getLogger(MailValidator.class);
    private final ImapMailClient client;

    @Inject
    public MailValidator(ImapMailClient client) {
        this.client = client;
    }

    /** Checks an exact subject, a plain-text body substring, and every expected filename. */
    public EmailContent validate(String subject, String expectedBody, List<String> expectedAttachmentNames,
            Duration timeout) throws MessagingException, IOException, InterruptedException {
        Objects.requireNonNull(expectedBody, "expectedBody");
        Objects.requireNonNull(expectedAttachmentNames, "expectedAttachmentNames");
        EmailContent email = client.awaitEmail(subject, timeout, Duration.ofSeconds(2));
        LOG.info(email.text());
        if (!email.text().contains(expectedBody)) {
            throw new AssertionError("Email with subject '" + subject + "' does not contain the expected body text");
        }
        for (String filename : expectedAttachmentNames) {
            if (!email.attachmentNames().contains(filename)) {
                throw new AssertionError("Expected attachment: " + filename
                        + "; received: " + email.attachmentNames());
            }
        }
        return email;
    }
}
