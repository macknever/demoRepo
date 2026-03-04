package com.lawrence.greenmail.util;

import static com.lawrence.greenmail.guice.module.MailModule.MAIL_CLIENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;

@Singleton
public class MailSender {
    private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);
    private final MailClient mailClient;

    @Inject
    public MailSender(@Named(MAIL_CLIENT) MailClient mailClient) {
        this.mailClient = mailClient;
    }

    public Future<JsonObject> send(MailMessage message) {
        return mailClient.sendMail(message)
                .onSuccess(result -> {
                    LOG.info("Mail {} sent successfully from {} to {}", message.getSubject(), message.getFrom(),
                            message.getTo());
                })
                .onFailure(result -> {
                    LOG.error("Mail {} sent failed", message.getSubject(), result.getCause());
                })
                .map(this::toResponseJson);
    }

    private JsonObject toResponseJson(MailResult result) {
        return new JsonObject()
                .put("messageId", result.getMessageID())
                .put("recipients", result.getRecipients());
    }
}

