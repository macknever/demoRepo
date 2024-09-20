package com.lawrence.guice;

import com.google.inject.Inject;
import com.lawrence.guice.module.NotificationAnnotationModule;

public class NotificationService {
    private final MessageFormatter messageFormatter;

    @Inject
    public NotificationService(@NotificationAnnotationModule.Formatter MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    public void sendNotification(String templateId, String messageData) {
        String formattedMessage = messageFormatter.formatMessage(templateId, messageData);
        System.out.println("Sending notification: " + formattedMessage);
    }
}

