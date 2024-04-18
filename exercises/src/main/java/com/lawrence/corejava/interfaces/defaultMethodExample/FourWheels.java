package com.lawrence.corejava.interfaces.defaultMethodExample;

public interface FourWheels {
    default void move() {
        System.out.println("four wheels are moving");
    }
}
