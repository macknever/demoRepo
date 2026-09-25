package com.lawrence.greenmail.util;

import java.io.IOException;
import java.time.Duration;

import com.google.inject.Inject;
import com.lawrence.greenmail.client.ImapMailClient;
import com.lawrence.greenmail.client.ImapMailClient.EmailContent;
import jakarta.mail.MessagingException;

/** Validates delivery; the caller owns and closes the injected client. */
public final class MailValidator {
    private final ImapMailClient client;

    @Inject
    public MailValidator(ImapMailClient client) {
        this.client = client;
    }

    /** Use null for the attachment name to check subject only; filenames match exactly. */
    public EmailContent validate(String subject, String expectedAttachmentName, Duration timeout)
            throws MessagingException, IOException, InterruptedException {
        EmailContent email = client.awaitEmail(subject, timeout, Duration.ofSeconds(2));
        if (expectedAttachmentName != null && !email.attachmentNames().contains(expectedAttachmentName)) {
            throw new AssertionError("Expected attachment: " + expectedAttachmentName
                    + "; received: " + email.attachmentNames());
        }
        return email;
    }

}
