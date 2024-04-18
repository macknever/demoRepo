package com.lawrence.corejava.interfaces.defaultMethodExample;

public interface SteamEngine {
    default void move() {
        System.out.println("Consuming gas");
    }
}
