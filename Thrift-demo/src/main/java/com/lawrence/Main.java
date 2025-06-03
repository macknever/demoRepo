package com.lawrence;

import com.lawrence.random.client.RandomServiceClientHttp;
import com.lawrence.random.client.RandomServiceClientTcp;

public class Main {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int tcpPort = 9090;
        final int httpPort = 9099;
        final String path = "thrift";

        final RandomServiceClientTcp randomServiceClient = new RandomServiceClientTcp(host, tcpPort);

        final int small = 10;
        final int large = 100;

        int ranTcp = randomServiceClient.getRandom(small, large);
        System.out.println(ranTcp);

//        final RandomServiceClientHttp randomServiceClientHttp = new RandomServiceClientHttp(host, httpPort, path);
//        int ranHttp = randomServiceClientHttp.getRandom(small, large);
//        System.out.println(ranHttp);


    }
}
