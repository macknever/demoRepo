package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lawrence.guice.MessageFormatter;
import com.lawrence.guice.MessageFormatterImpl;
import com.lawrence.guice.MessageTemplateLoader;
import com.lawrence.guice.MessageTemplateLoaderImpl;

public class NotificationModule extends AbstractModule {

    @Provides
    MessageFormatter provideMessageFormatter(MessageFormatterImpl messageFormatterImpl) {
        return messageFormatterImpl;
    }

    @Provides
    MessageTemplateLoader provideMessageTemplateLoader(MessageTemplateLoaderImpl messageTemplateLoaderImpl) {
        return messageTemplateLoaderImpl;
    }

}
