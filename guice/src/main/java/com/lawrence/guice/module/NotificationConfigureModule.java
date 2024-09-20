package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.lawrence.guice.MessageFormatter;
import com.lawrence.guice.MessageFormatterImpl;
import com.lawrence.guice.MessageTemplateLoader;
import com.lawrence.guice.MessageTemplateLoaderImpl;

public class NotificationConfigureModule extends AbstractModule {

    @Override
    public void configure() {
        bind(MessageTemplateLoader.class).to(MessageTemplateLoaderImpl.class);
        bind(MessageFormatter.class).to(MessageFormatterImpl.class);
    }
}
