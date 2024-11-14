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
 * Each of the agents in charge of receive and broadcast messages.
 */
public class ServerAgent implements Callable<Void> {
    private static final Logger log = LoggerFactory.getLogger(ServerAgent.class);
    private final Subscription subscription;
    private final ExclusivePublication publication;
    private final byte[] dataInput;
    private final UnsafeBuffer bufferOutput;
    private final FragmentHandler fragmentHandler;
    
    public ServerAgent(final Subscription subscription,
            final ExclusivePublication publication, 
            final int dataInputSize) {
        this.subscription = subscription;
        this.publication = publication;
        this.dataInput = new byte[dataInputSize];

        this.bufferOutput = new UnsafeBuffer(
                BufferUtil.allocateDirectAligned(256, dataInputSize));
        // Define a fragment handler to process received messages
        this.fragmentHandler = new FragmentHandler() {
            @Override
            public void onFragment(DirectBuffer buffer, int offset, int length, Header header) {
                buffer.getBytes(offset, dataInput);
                // Re-publish the message to the broadcast channel
                bufferOutput.wrap(dataInput, 0, length);
                long result = publication.offer(bufferOutput);
            }
        };
    }

    public Void call() {
        final IdleStrategy idleStrategy = new BusySpinIdleStrategy();

        // Keep polling the subscription for messages and handle them
        log.info("Agent " + subscription + "/" + publication + " running");
        while (true) {
            int fragmentsRead = subscription.poll(this.fragmentHandler, 1);
            idleStrategy.idle(fragmentsRead);
        }
        //return null;
    }
}

