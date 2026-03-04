package com.lawrence.greenmail.util;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.lawrence.greenmail.model.ReportRequest;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailMessage;

@Singleton
public class ReportEmailComposer {
    private static final Logger LOG = LoggerFactory.getLogger(ReportEmailComposer.class);
    private static final String COMMA = ",";

    public MailMessage compose(ReportRequest req) {
        String text = "Title: " + nullToEmpty(req.subject()) + "\n\n" + nullToEmpty(req.body());
        MailMessage msg = new MailMessage()
                .setBounceAddress(req.bounceFrom())
                .setFrom(req.from())
                .setTo(req.to())
                .setSubject(req.subject())
                .setText(text);

        if (req.cc() != null) {
            msg.setCc(Arrays.stream(req.cc().split(COMMA)).toList());
        }

        if (req.size() != 0) {
            var bytes = new byte[req.size()];

            MailAttachment attachment = MailAttachment.create()
                    .setContentType("text/plain")
                    .setData(Buffer.buffer(bytes));

            msg.setAttachment(attachment);
        }

        LOG.info("Email bounceAddress: {}", msg.getBounceAddress());
        LOG.info("cc:{}", msg.getCc());

        return msg;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}