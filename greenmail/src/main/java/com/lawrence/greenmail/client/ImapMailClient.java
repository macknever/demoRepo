package com.lawrence.greenmail.client;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Read-only IMAPS client. Reading mail never marks it seen or deletes it.
 */
public final class ImapMailClient implements AutoCloseable {
    private static final Pattern END_CRLF = Pattern.compile("[\\r\\n]+$");
    private final Store store;
    private final String folderName;
    private static final Logger LOG = LoggerFactory.getLogger(ImapMailClient.class);

    ImapMailClient(Store store, String folderName) {
        this.store = Objects.requireNonNull(store);
        this.folderName = requireText(folderName, "folderName");
    }

    /**
     * Connect using IMAPS and mailbox credentials.
     */
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
            LOG.info("Connecting to IMAPS server {}:{}", host, port);
            store.connect(host, port, username, password);
            LOG.info("IMAPS connection established");
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
                .onRetry(event -> LOG.info("Matching email not received yet; retry attempt {}", event.getAttemptCount()))
                .withMaxAttempts(-1)
                .withMaxDuration(timeout)
                .withDelayFn(event -> pollInterval)
                .build();
        LOG.info("Waiting up to {} for an exact subject match", timeout);
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
            LOG.info("Opening mailbox folder {}", folderName);
            folder.open(Folder.READ_ONLY);
            LOG.info("Searching mailbox by subject");
            Message[] matches = folder.search(new SubjectTerm(subject));
            LOG.info("Subject search returned {} candidates", matches.length);
            // Highest mailbox sequence number is the most recently appended message.
            Arrays.sort(matches, Comparator.comparingInt(Message::getMessageNumber).reversed());
            for (Message message : matches) {
                if (subject.equals(message.getSubject())) {
                    LOG.info("Exact subject matched; reading body and attachment names");
                    if (Boolean.getBoolean("smoke.imap.logRaw")) {
                        LOG.info("Reading full raw MIME message, including attachment payloads");
                        var raw = new ByteArrayOutputStream();
                        message.writeTo(raw);
                        LOG.info("Raw email:\n{}", raw.toString(StandardCharsets.UTF_8));
                        LOG.info("Raw MIME logging complete");
                    }
                    EmailContent email = readContent(message);
                    LOG.info("Email parsed: {} body characters, {} attachments",
                            email.text().length(), email.attachmentNames().size());
                    return email;
                }
            }
            return null;
        } finally {
            if (folder.isOpen()) {
                LOG.info("Closing mailbox folder");
                folder.close(false);
                LOG.info("Mailbox folder closed");
            }
        }
    }

    static EmailContent readContent(Message message) throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        List<String> attachments = new ArrayList<>();
        readPart(message, text, attachments);
        // Remove trailing line endings while preserving spaces and line breaks within the body.
        String body = END_CRLF.matcher(text.toString()).replaceFirst("");
        return new EmailContent(message.getSubject(), body, List.copyOf(attachments));
    }

    private static void readPart(Part part, StringBuilder text, List<String> attachments)
            throws MessagingException, IOException {
        String disposition = part.getDisposition();
        switch (disposition == null ? "" : disposition.toLowerCase(Locale.ROOT)) {
            case Part.ATTACHMENT -> {
                String fileName = part.getFileName();
                if (fileName != null) {
                    attachments.add(MimeUtility.decodeText(fileName));
                }
            }
            default -> {
                if (part.isMimeType("text/plain")) {
                    text.append((String) part.getContent()).append('\n');
                } else if (part.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) part.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        readPart(multipart.getBodyPart(i), text, attachments);
                    }
                }
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
        LOG.info("Closing IMAPS connection");
        store.close();
        LOG.info("IMAPS connection closed");
    }

    public record EmailContent(String subject, String text, List<String> attachmentNames) {
    }
}
