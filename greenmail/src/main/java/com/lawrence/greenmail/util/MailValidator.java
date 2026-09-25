package com.lawrence.greenmail.util;

import java.io.IOException;
import java.time.Duration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.lawrence.greenmail.client.ImapMailClient;
import com.lawrence.greenmail.client.ImapMailClient.EmailContent;
import com.lawrence.greenmail.guice.module.ImapMailModule;
import jakarta.mail.MessagingException;

/** Validates delivery; the caller owns and closes the injected client. */
public final class MailValidator {
    private final ImapMailClient client;

    @Inject
    public MailValidator(ImapMailClient client) {
        this.client = client;
    }

    public EmailContent validate(String subject, String expectedText, Duration timeout)
            throws MessagingException, IOException, InterruptedException {
        EmailContent email = client.awaitEmail(subject, timeout, Duration.ofSeconds(2));
        if (expectedText != null && !email.text().contains(expectedText)) {
            throw new AssertionError("Delivered email does not contain the expected plain-text body");
        }
        return email;
    }

    /** Run with an exact subject and, optionally, a plain-text body substring. */
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2 || args[0].isBlank()) {
            throw new IllegalArgumentException("Usage: MailValidator <exact subject> [expected body text]");
        }
        var injector = Guice.createInjector(new ImapMailModule());
        try (var client = injector.getInstance(ImapMailClient.class)) {
            new MailValidator(client).validate(args[0], args.length == 2 ? args[1] : null, Duration.ofSeconds(60));
            System.out.println("Email delivery validated successfully.");
        }
    }
}
