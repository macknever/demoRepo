package com.lawrence.eventlistener;

import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

public class EventService {
    private Set<EventListener> eventListeners;
    private Map<String, EventListener> eventListenerMap;

    @Inject
    public EventService(Set<EventListener> eventListeners, Map<String, EventListener> eventListenerMap) {
        this.eventListeners = eventListeners;
        this.eventListenerMap = eventListenerMap;

    }

    public void fireEvent(String event) {
        for (EventListener listener : eventListeners) {
            listener.handleEvent(event);
        }
    }

    public void fireEvent(String listenerType, String event) {
        EventListener eventListener = eventListenerMap.get(listenerType);
        if (eventListener == null) {
            System.out.println("No such event listener");
        } else {
            eventListener.handleEvent(event);
        }
    }
}
