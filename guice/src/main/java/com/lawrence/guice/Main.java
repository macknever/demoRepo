package com.lawrence.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lawrence.eventlistener.EventService;
import com.lawrence.guice.module.EventListenerModule;
import com.lawrence.guice.module.NotificationModule;
import com.lawrence.notification.NotificationService;
import com.lawrence.notification.ValidationPrinter;

public class Main {

    public static void main(String[] args) {

        Injector injectorFromProvide = Guice.createInjector(new NotificationModule());
        //Injector injectorFromConfigure = Guice.createInjector(new NotificationConfigureModule());
        // ** bindings with annotation or name cant work with other guice module that provides default binding with
        // no name.
        //Injector injectorFromAnnotation = Guice.createInjector(new NotificationAnnotationModule());


        NotificationService nsFromProvide = injectorFromProvide.getInstance(NotificationService.class);
        // NotificationService nsFromConfigure = injectorFromConfigure.getInstance(NotificationService.class);
        //NotificationService nsFromAnnotation = injectorFromAnnotation.getInstance(NotificationService.class);

        nsFromProvide.sendNotification("temp", "message");
        //nsFromConfigure.sendNotification("NFL", "Go Canada GO");
        //nsFromAnnotation.sendNotification("Spurs", "Go spurs go");

        ValidationPrinter printer = injectorFromProvide.getInstance(ValidationPrinter.class);
        printer.print();

        //==================== Multibinding ====================
        Injector eventListenerInjector = Guice.createInjector(new EventListenerModule());
        EventService service = eventListenerInjector.getInstance(EventService.class);
        service.fireEvent("user log in");
        service.fireEvent("user log out");
        service.fireEvent("login", "another user log in");
    }
}
