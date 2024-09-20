package com.lawrence.guice;

public class MessageTemplateLoaderImpl implements MessageTemplateLoader {

    @Override
    public String loadTemplate(String templateId) {
        return "Loaded template for " + templateId;
    }
}
