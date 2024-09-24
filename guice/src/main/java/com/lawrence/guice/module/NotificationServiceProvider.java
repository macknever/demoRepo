package com.lawrence.guice.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lawrence.notification.MessageFormatter;
import com.lawrence.notification.NotificationService;

public class NotificationServiceProvider implements Provider<NotificationService> {

    private MessageFormatter formatter;

    @Inject
    NotificationServiceProvider(MessageFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public NotificationService get() {
        return new NotificationService(formatter);
    }
}
