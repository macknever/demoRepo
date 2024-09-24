package com.lawrence.notification;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MessageFormatterValidationPrinter implements ValidationPrinter {

    private MessageFormatter formatter;
    private String templateId;
    private String messageData;

    @Inject
    public MessageFormatterValidationPrinter(MessageFormatter messageFormatter, @Named("templateId") String templateId,
            @Named("messageData") String messageData) {
        this.formatter = messageFormatter;
        this.templateId = templateId;
        this.messageData = messageData;
    }

    @Override
    public void print() {
        System.out.println(formatter.formatMessage(templateId, messageData));
    }
}
