package com.lawrence.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lawrence.guice.module.NotificationConfigureModule;
import com.lawrence.guice.module.NotificationModule;

public class Main {
    public static void main(String[] args) {
        Injector injectorFromProvide = Guice.createInjector(new NotificationModule());
        Injector injectorFromConfigure = Guice.createInjector(new NotificationConfigureModule());
        // ** bindings with annotation or name cant work with other guice module that provides default binding with
        // no name.
        //Injector injectorFromAnnotation = Guice.createInjector(new NotificationAnnotationModule());


        NotificationService nsFromProvide = injectorFromProvide.getInstance(NotificationService.class);
        NotificationService nsFromConfigure = injectorFromConfigure.getInstance(NotificationService.class);
        //NotificationService nsFromAnnotation = injectorFromAnnotation.getInstance(NotificationService.class);

        nsFromProvide.sendNotification("NBA", "This is my house");
        nsFromConfigure.sendNotification("NFL", "Go Canada GO");
        //nsFromAnnotation.sendNotification("Spurs", "Go spurs go");
    }
}
