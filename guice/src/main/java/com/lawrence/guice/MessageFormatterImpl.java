package com.lawrence.guice;

import com.google.inject.Inject;

public class MessageFormatterImpl implements MessageFormatter {
    private final MessageTemplateLoader templateLoader;

    @Inject
    public MessageFormatterImpl(MessageTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    @Override
    public String formatMessage(String templateId, String messageData) {
        String template = templateLoader.loadTemplate(templateId);
        return template + " with data: " + messageData;
    }
}
