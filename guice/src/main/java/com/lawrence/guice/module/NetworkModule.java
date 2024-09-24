package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lawrence.network.IpAddress;
import com.lawrence.network.NetworkPermit;

@NetworkPermit
public final class NetworkModule extends AbstractModule {

    @Provides
    @IpAddress
    int provideIpAddress() {
        // This is an example IP address. In a real-world scenario,
        // you might pull this from configuration or an external service.
        return 19216811; // Representing 192.168.1.1
    }

    @Override
    protected void configure() {
        // Install RoutingModule, which will be permitted to provide RoutingTable
        install(new RoutingModule());
    }
}

