package com.lawrence.greenmail.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lawrence.greenmail.client.ImapMailClient;
import jakarta.mail.MessagingException;

/** Separate from the SMTP application module; connections are opened only when requested. */
public final class ImapMailModule extends AbstractModule {
    @Provides
    ImapMailClient imapMailClient() throws MessagingException {
        String password = System.getenv("SMOKE_IMAP_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Set SMOKE_IMAP_PASSWORD before running the validator");
        }
        return new ImapMailClient(
                environment("SMOKE_IMAP_HOST", "carbonio.dev-globalrelay.net"),
                Integer.parseInt(environment("SMOKE_IMAP_PORT", "993")),
                environment("SMOKE_IMAP_USERNAME", "nucleus.uc@dev-globalrelay.net"),
                password, environment("SMOKE_IMAP_FOLDER", "INBOX"));
    }

    private static String environment(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
