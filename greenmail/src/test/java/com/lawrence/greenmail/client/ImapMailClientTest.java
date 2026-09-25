package com.lawrence.greenmail.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.time.Duration;
import jakarta.mail.Folder;
import jakarta.mail.Store;
import jakarta.mail.Message;
import jakarta.mail.search.SearchTerm;

import org.junit.jupiter.api.Test;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

class ImapMailClientTest {
    @Test
    void requiresStartTlsWithTheCorrectProtocolProperties() {
        var properties = ImapMailClient.mailProperties(true);
        assertThat(properties).containsEntry("mail.imap.starttls.enable", "true")
                .containsEntry("mail.imap.starttls.required", "true")
                .containsEntry("mail.imap.ssl.checkserveridentity", "true")
                .containsEntry("mail.imap.peek", "true");
        assertThat(ImapMailClient.mailProperties(false))
                .containsEntry("mail.imaps.ssl.checkserveridentity", "true")
                .doesNotContainKey("mail.imap.starttls.enable");
    }

    @Test
    void decodesNestedMimeWithoutTreatingTextAttachmentsAsBody() throws Exception {
        String raw = """
                From: sender@example.com
                To: nucleus.uc@dev-globalrelay.net
                Subject: smoke-123
                Message-ID: <smoke-123@example.com>
                MIME-Version: 1.0
                Content-Type: multipart/mixed; boundary=outer

                --outer
                Content-Type: multipart/alternative; boundary=inner

                --inner
                Content-Type: text/plain; charset=UTF-8
                Content-Transfer-Encoding: base64

                RGVzY3JpcHRpb246IHNtb2tlLTEyMw==
                --inner
                Content-Type: text/html; charset=UTF-8

                <p>Description: smoke-123</p>
                --inner--
                --outer
                Content-Type: text/plain
                Content-Disposition: attachment; filename="notes.txt"

                attachment-only-marker
                --outer--
                """;
        var content = ImapMailClient.readContent(message(raw));
        assertThat(content.subject()).isEqualTo("smoke-123");
        assertThat(content.messageId()).isEqualTo("<smoke-123@example.com>");
        assertThat(content.text()).contains("Description: smoke-123")
                .doesNotContain("attachment-only-marker", "<p>");
        assertThat(content.attachmentNames()).containsExactly("notes.txt");
    }

    @Test
    void decodesQuotedPrintablePlainTextWithoutAttachments() throws Exception {
        var content = ImapMailClient.readContent(message("""
                Subject: smoke-456
                MIME-Version: 1.0
                Content-Type: text/plain; charset=UTF-8
                Content-Transfer-Encoding: quoted-printable

                Description: caf=C3=A9 smoke-456
                """));
        assertThat(content.text()).contains("Description: café smoke-456");
        assertThat(content.attachmentNames()).isEmpty();
        assertThat(content.messageId()).isNull();
    }

    @Test
    void pollsUntilExactSubjectArrivesAndClosesReadOnlyFolder() throws Exception {
        Store store = mock(Store.class);
        Folder folder = mock(Folder.class);
        when(store.getFolder("INBOX")).thenReturn(folder);
        when(folder.isOpen()).thenReturn(true);
        Message wrongSubject = message("Subject: prefix smoke-123\nTo: nucleus.uc@dev-globalrelay.net\n\nwrong");
        Message expected = message("Subject: smoke-123\n\nexpected");
        when(folder.search(any(SearchTerm.class))).thenReturn(
                new Message[] {wrongSubject}, new Message[] {expected});
        try (var client = new ImapMailClient(store, "INBOX")) {
            assertThat(client.awaitEmail("smoke-123",
                    Duration.ofSeconds(1), Duration.ofMillis(1)).text()).contains("expected");
        }
        verify(folder, times(2)).open(Folder.READ_ONLY);
        verify(folder, times(2)).close(false);
        verify(store).close();
    }

    @Test
    void timesOutWhenMailboxNeverContainsMatchingEmail() throws Exception {
        Store store = mock(Store.class);
        Folder folder = mock(Folder.class);
        when(store.getFolder("INBOX")).thenReturn(folder);
        when(folder.isOpen()).thenReturn(true);
        when(folder.search(any(SearchTerm.class))).thenReturn(new Message[0]);
        try (var client = new ImapMailClient(store, "INBOX")) {
            assertThatThrownBy(() -> client.awaitEmail("smoke-absent",
                    Duration.ofMillis(10), Duration.ofMillis(1)))
                    .isInstanceOf(java.io.IOException.class).hasMessageContaining("Timed out");
        }
        verify(folder, atLeastOnce()).close(false);
        verify(store).close();
    }

    private static MimeMessage message(String raw) throws Exception {
        return new MimeMessage(Session.getInstance(new Properties()),
                new ByteArrayInputStream(raw.replace("\n", "\r\n").getBytes(StandardCharsets.UTF_8)));
    }
}
