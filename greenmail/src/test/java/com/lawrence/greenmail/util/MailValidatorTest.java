package com.lawrence.greenmail.util;

import java.time.Duration;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.lawrence.greenmail.client.ImapMailClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailValidatorTest {
    @Test
    void validatesBodyUsingInjectedClient() throws Exception {
        var client = mock(ImapMailClient.class);
        var email = new ImapMailClient.EmailContent("unique-subject", "id", "report complete", List.of());
        when(client.awaitEmail("unique-subject", Duration.ofSeconds(60), Duration.ofSeconds(2)))
                .thenReturn(email);
        var injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ImapMailClient.class).toInstance(client);
            }
        });
        var validator = injector.getInstance(MailValidator.class);
        assertThat(validator.validate("unique-subject", "complete", Duration.ofSeconds(60))).isEqualTo(email);
        assertThat(validator.validate("unique-subject", null, Duration.ofSeconds(60))).isEqualTo(email);
        assertThatThrownBy(() -> validator.validate("unique-subject", "missing", Duration.ofSeconds(60)))
                .isInstanceOf(AssertionError.class).hasMessageContaining("expected plain-text body");
    }
}
