package com.lawrence.corejava.interfaces.defaultMethodExample;

import org.junit.jupiter.api.Test;

class DefaultMethodTest {

    @Test
    void methodConflictNeedsToResolve() {
        class SteamEngineVehicle extends Vehicle implements SteamEngine {
            @Override
            public void move() {
            }
        }
        SteamEngineVehicle vehicle = new SteamEngineVehicle();
        vehicle.move();

    }
}
