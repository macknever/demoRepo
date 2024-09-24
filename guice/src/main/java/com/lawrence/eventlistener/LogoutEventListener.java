package com.lawrence.eventlistener;

public class LogoutEventListener implements EventListener {
    @Override
    public void handleEvent(String event) {
        if (event.toLowerCase().contains("log out")) {
            System.out.println("Log out event: " + event);
        }
    }
}
