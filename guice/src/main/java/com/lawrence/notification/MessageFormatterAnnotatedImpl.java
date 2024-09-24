package com.lawrence.notification;

import com.google.inject.Inject;
import com.lawrence.guice.module.NotificationAnnotationModule;

public class MessageFormatterAnnotatedImpl implements MessageFormatter {

    private final MessageTemplateLoader templateLoader;

    @Inject
    public MessageFormatterAnnotatedImpl(@NotificationAnnotationModule.Loader MessageTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    @Override
    public String formatMessage(String templateId, String messageData) {
        String template = templateLoader.loadTemplate(templateId);
        return template + " with data: " + messageData;
    }
}
