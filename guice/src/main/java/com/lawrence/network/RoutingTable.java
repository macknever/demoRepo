package com.lawrence.network;

import com.google.inject.RestrictedBindingSource;

@RestrictedBindingSource(
        explanation = "Please install NetworkModule instead of binding network bindings yourself.",
        permits = { NetworkPermit.class }
)
public interface RoutingTable {
    int getNextHopIpAddress(int destinationIpAddress);
}
