package com.lawrence.greenmail.client;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.RetryPolicy;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.SubjectTerm;

/** Read-only IMAPS client. Reading mail never marks it seen or deletes it. */
public final class ImapMailClient implements AutoCloseable {
    private final Store store;
    private final String folderName;

    ImapMailClient(Store store, String folderName) {
        this.store = Objects.requireNonNull(store);
        this.folderName = requireText(folderName, "folderName");
    }

    /** Connect using IMAPS and mailbox credentials. */
    public ImapMailClient(String host, int port, String username, String password,
            String folderName) throws MessagingException {
        this.folderName = requireText(folderName, "folderName");
        requireText(host, "host");
        requireText(username, "username");
        requireText(password, "password");
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid IMAP port");
        }
        store = Session.getInstance(mailProperties()).getStore("imaps");
        try {
            store.connect(host, port, username, password);
        } catch (MessagingException failure) {
            try {
                store.close();
            } catch (MessagingException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    static Properties mailProperties() {
        String prefix = "mail.imaps.";
        Properties properties = new Properties();
        properties.setProperty(prefix + "ssl.checkserveridentity", "true");
        properties.setProperty(prefix + "peek", "true");
        properties.setProperty(prefix + "connectiontimeout", "10000");
        properties.setProperty(prefix + "timeout", "10000");
        properties.setProperty(prefix + "writetimeout", "10000");
        return properties;
    }

    /**
     * Polls the connected mailbox for an exact subject. Use a fresh UUID in the subject for each run.
     * Authentication and protocol errors fail immediately; only absence of mail is retried.
     * The delivery deadline is checked between polls; in-flight operations have 10-second socket timeouts.
     */
    public EmailContent awaitEmail(String subject, Duration timeout, Duration pollInterval)
            throws MessagingException, IOException, InterruptedException {
        requireText(subject, "subject");
        if (timeout.isNegative() || timeout.isZero() || pollInterval.isNegative() || pollInterval.isZero()) {
            throw new IllegalArgumentException("Timeout and poll interval must be positive");
        }
        RetryPolicy<EmailContent> retryPolicy = RetryPolicy.<EmailContent>builder()
                .handleIf((result, failure) -> failure == null && result == null)
                .withMaxAttempts(-1)
                .withMaxDuration(timeout)
                .withDelayFn(event -> pollInterval)
                .build();
        try {
            EmailContent email = Failsafe.with(retryPolicy).get(() -> findEmail(subject));
            if (email == null) {
                throw new IOException("Timed out waiting for email with subject: " + subject);
            }
            return email;
        } catch (FailsafeException failure) {
            // Preserve the client's checked-exception API rather than exposing library wrappers.
            if (failure.getCause() instanceof InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw interrupted;
            }
            if (failure.getCause() instanceof MessagingException messaging) {
                throw messaging;
            }
            if (failure.getCause() instanceof IOException io) {
                throw io;
            }
            throw failure;
        }
    }

    private EmailContent findEmail(String subject) throws MessagingException, IOException, InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Email polling interrupted");
        }
        // Reopen each attempt so newly delivered messages are visible.
        Folder folder = store.getFolder(folderName);
        try {
            folder.open(Folder.READ_ONLY);
            for (Message message : folder.search(new SubjectTerm(subject))) {
                if (subject.equals(message.getSubject())) {
                    return readContent(message);
                }
            }
            return null;
        } finally {
            if (folder.isOpen()) {
                folder.close(false);
            }
        }
    }

    static EmailContent readContent(Message message) throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        List<String> attachments = new ArrayList<>();
        readPart(message, text, attachments);
        return new EmailContent(message.getSubject(), text.toString(), List.copyOf(attachments));
    }

    private static void readPart(Part part, StringBuilder text, List<String> attachments)
            throws MessagingException, IOException {
        String fileName = part.getFileName();
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || fileName != null) {
            if (fileName != null) {
                attachments.add(MimeUtility.decodeText(fileName));
            }
        } else if (part.isMimeType("text/plain")) {
            text.append((String) part.getContent()).append('\n');
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                readPart(multipart.getBodyPart(i), text, attachments);
            }
        }
    }

    private static String requireText(String value, String name) {
        if (Objects.requireNonNull(value, name).isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    @Override
    public void close() throws MessagingException {
        store.close();
    }

    public record EmailContent(String subject, String text, List<String> attachmentNames) {
    }
}
