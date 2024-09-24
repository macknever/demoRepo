package com.lawrence.notification;

public class NotificationService {
    private final MessageFormatter messageFormatter;

    public NotificationService(MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    public void sendNotification(String templateId, String messageData) {
        String formattedMessage = messageFormatter.formatMessage(templateId, messageData);
        System.out.println("Sending notification: " + formattedMessage);
    }
}

