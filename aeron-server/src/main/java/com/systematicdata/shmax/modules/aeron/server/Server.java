package com.systematicdata.shmax.modules.aeron.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.*;
import org.slf4j.*;

import io.aeron.Aeron;
import io.aeron.ExclusivePublication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.BufferUtil;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.charset.StandardCharsets;

/**
 * Future improvements:
 *      - Use Multicast 224.0.1.1 and so (config of OS?)
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        final String configName = args[0];
        new Server(configName);
    }


    public Server(final String configName) throws IOException {
        final Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader()
                .getResourceAsStream("application.properties"));
        log.info("Loaded properties:");
        for(String key : properties.stringPropertyNames()) {
            log.info("\t" + key + ":" + properties.getProperty(key));
        }

        final String configFile = properties.getProperty("aeron.server." + configName 
                + ".configFile");
        log.info("Loading config file: " + configFile);
        properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile));
        log.info("Loaded complete properties:");
        for(String key : properties.stringPropertyNames()) {
            log.info("\t" + key + ":" + properties.getProperty(key));
        }




        // TODO: Add help in case of invalid configuration.

        final String receiveChannel = properties.getProperty("aeron.server.receiveChannel");
        final String broadcastChannel = properties.getProperty("aeron.server.broadcastChannel");
        final List<String> streamIds = Arrays.asList(
                properties.getProperty("aeron.server.streamIds").split(","));
        final int dataSize = Integer.parseInt(properties.getProperty("aeron.server.dataSize"));


        MediaDriver mediaDriver = MediaDriver.launchEmbedded();
        Aeron.Context context = new Aeron.Context().aeronDirectoryName(
                mediaDriver.aeronDirectoryName());
        
        try(Aeron aeron = Aeron.connect(context);
                    var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final List<ServerAgent> agents = new ArrayList<ServerAgent>();
            for(final String streamId : streamIds) {
                log.info("Creating agent : Subscription channel: " + receiveChannel
                        + ", Publich channel: " + broadcastChannel
                        + ", Stream Ids: " + streamId
                        + ", buffer data size: " + dataSize + "bytes");

                final int istream = Integer.parseInt(streamId);
                agents.add(new ServerAgent(
                        aeron.addSubscription(receiveChannel, istream),
                        aeron.addExclusivePublication(broadcastChannel, istream),
                        dataSize));
            }
            for(final ServerAgent agent : agents) {
                scope.fork(agent);
            }

            scope.join();
            scope.throwIfFailed();
            log.info("Shutting down the server.");
        } catch(ExecutionException | InterruptedException ee) {
            log.error("Exception in agent thread.", ee);
            throw new IOException(ee);
        } finally {
            mediaDriver.close();
        }
    }

    private class ServerAgent implements Callable<Void> {
        private final Subscription subscription;
        private final ExclusivePublication publication;
        private final byte[] data;
        private final UnsafeBuffer unsafeBuffer;
        private final FragmentHandler fragmentHandler;
        
        public ServerAgent(final Subscription subscription,
                final ExclusivePublication publication, 
                final int dataSize) {
            this.subscription = subscription;
            this.publication = publication;
            this.data = new byte[dataSize];

            this.unsafeBuffer = new UnsafeBuffer(
                    BufferUtil.allocateDirectAligned(256, dataSize));
            // Define a fragment handler to process received messages
            this.fragmentHandler = new FragmentHandler() {
                @Override
                public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
                    buffer.getBytes(offset, data);
                    // Re-publish the message to the broadcast channel
                    unsafeBuffer.wrap(data, 0, length);
                    long result = publication.offer(unsafeBuffer);
                }
            };
        }

        public Void call() {
            final IdleStrategy idleStrategy = new BusySpinIdleStrategy();

            // Keep polling the subscription for messages and handle them
            log.info("Server " + subscription + "/" + publication + " running");
            while (true) {
                int fragmentsRead = subscription.poll(this.fragmentHandler, 1);
                idleStrategy.idle(fragmentsRead);
            }
            //return null;
        }
    }
}

