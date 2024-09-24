package com.lawrence.notification;

import com.google.inject.ImplementedBy;

@ImplementedBy(MessageFormatterValidationPrinter.class)
public interface ValidationPrinter {
    public void print();
}
