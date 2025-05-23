package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.lawrence.eventlistener.EventListener;
import com.lawrence.eventlistener.LoginEventListener;

public class EventListenerModule extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<EventListener> eventBinder = Multibinder.newSetBinder(binder(), EventListener.class);
        eventBinder.addBinding().to(LoginEventListener.class);


        MapBinder<String, EventListener> eventListenerMapBinder = MapBinder.newMapBinder(binder(), String.class,
                EventListener.class);
        eventListenerMapBinder.addBinding("login").to(LoginEventListener.class);
    }

}
