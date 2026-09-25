package com.lawrence.greenmail.client;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.SubjectTerm;

/** Read-only IMAP client with TLS. Reading mail never marks it seen or deletes it. */
public final class ImapMailClient implements AutoCloseable {
    private final Store store;
    private final String folderName;

    ImapMailClient(Store store, String folderName) {
        this.store = Objects.requireNonNull(store);
        this.folderName = requireText(folderName, "folderName");
    }

    /** Connect using implicit TLS or required STARTTLS and mailbox credentials. */
    public ImapMailClient(String host, int port, String username, String password,
            String folderName, boolean startTls) throws MessagingException {
        this.folderName = requireText(folderName, "folderName");
        requireText(host, "host");
        requireText(username, "username");
        requireText(password, "password");
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid IMAP port");
        }
        String protocol = startTls ? "imap" : "imaps";
        store = Session.getInstance(mailProperties(startTls)).getStore(protocol);
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

    static Properties mailProperties(boolean startTls) {
        String prefix = startTls ? "mail.imap." : "mail.imaps.";
        Properties properties = new Properties();
        properties.setProperty(prefix + "ssl.checkserveridentity", "true");
        properties.setProperty(prefix + "peek", "true");
        properties.setProperty(prefix + "connectiontimeout", "10000");
        properties.setProperty(prefix + "timeout", "10000");
        properties.setProperty(prefix + "writetimeout", "10000");
        if (startTls) {
            properties.setProperty(prefix + "starttls.enable", "true");
            properties.setProperty(prefix + "starttls.required", "true");
        }
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
        long started = System.nanoTime();
        long timeoutNanos = timeout.toNanos();
        do {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Email polling interrupted");
            }
            // Reopen each poll so newly delivered messages are visible without relying on cached counts.
            Folder folder = store.getFolder(folderName);
            try {
                folder.open(Folder.READ_ONLY);
                for (Message message : folder.search(new SubjectTerm(subject))) {
                    if (subject.equals(message.getSubject())) {
                        return readContent(message);
                    }
                }
            } finally {
                if (folder.isOpen()) {
                    folder.close(false);
                }
            }
            long remaining = timeoutNanos - (System.nanoTime() - started);
            if (remaining <= 0) {
                break;
            }
            try {
                Thread.sleep(Duration.ofNanos(Math.min(remaining, pollInterval.toNanos())));
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw interrupted;
            }
        } while (System.nanoTime() - started < timeoutNanos);
        throw new IOException("Timed out waiting for email with subject: " + subject);
    }

    static EmailContent readContent(Message message) throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        List<String> attachments = new ArrayList<>();
        readPart(message, text, attachments);
        String[] ids = message.getHeader("Message-ID");
        return new EmailContent(message.getSubject(), ids == null ? null : ids[0], text.toString(),
                List.copyOf(attachments));
    }

    private static void readPart(Part part, StringBuilder text, List<String> attachments)
            throws MessagingException, IOException {
        String fileName = part.getFileName();
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || fileName != null) {
            // An attached text file must not accidentally satisfy assertions about the message body.
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

    public record EmailContent(String subject, String messageId, String text, List<String> attachmentNames) {
    }
}
