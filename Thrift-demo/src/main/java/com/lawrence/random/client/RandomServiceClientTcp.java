package com.lawrence.random.client;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.random.randomservice.InvalidRandomRange;
import com.lawrence.random.randomservice.RandomNumberStruct;
import com.lawrence.random.randomservice.RandomService;
import com.lawrence.random.randomservice.Range;

/**
 * Simple Java client for the RandomService Thrift service.
 */
public class RandomServiceClientTcp implements RandomNumberService {
    private static final Logger LOG = LoggerFactory.getLogger(RandomServiceClientTcp.class);
    private final String host;
    private final int port;

    public RandomServiceClientTcp(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int getRandom(int min, int max) {
        final Range range = new Range(min, max);
        int result = -Integer.MAX_VALUE;
        try (TTransport transport = new TSocket(host, port)) {
            transport.open();

            // Use TBinaryProtocol for serialization
            TProtocol protocol = new TBinaryProtocol(transport);

            // Create a client to use the protocol encoder
            RandomService.Client client = new RandomService.Client(protocol);

            // Call the remote roll() method
            RandomNumberStruct random = client.roll(range);

            LOG.info("Rolled value = {} createdAt: {}\n", random.getRandomNumber(), random.getCreatedAt());
            result = random.getRandomNumber();
        } catch (InvalidRandomRange irr) {
            LOG.error("Invalid range provided: {}", irr.getDesc());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

