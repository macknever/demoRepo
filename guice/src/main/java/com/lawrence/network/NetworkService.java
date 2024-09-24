package com.lawrence.network;

import com.google.inject.Inject;

public class NetworkService {
    private final int ipAddress;
    private final RoutingTable routingTable;

    @Inject
    public NetworkService(@IpAddress int ipAddress, RoutingTable routingTable) {
        this.ipAddress = ipAddress;
        this.routingTable = routingTable;
    }

    public void printNetworkInfo(int destinationIp) {
        System.out.println("Our IP Address: " + ipAddress);
        System.out.println("Next hop for " + destinationIp + ": " + routingTable.getNextHopIpAddress(destinationIp));
    }
}

