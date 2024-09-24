package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lawrence.network.RoutingTable;

public final class RoutingModule extends AbstractModule {
    @Provides
    RoutingTable provideRoutingTable() {
        return new RoutingTable() {
            @Override
            public int getNextHopIpAddress(int destinationIpAddress) {
                // A simple example of a next-hop lookup.
                // In a real system, this would involve routing logic.
                return destinationIpAddress + 1;
            }
        };
    }
}

