package com.lawrence.eventlistener;

public class LoginEventListener implements EventListener {

    @Override
    public void handleEvent(String event) {
        if (event.toLowerCase().contains("log in")) {
            System.out.println("Log in event: " + event);
        }
    }
}
