package com.systematicdata.shmax.bus.aeron;

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
import com.systematicdata.shmax.bus.*;


/**
 * Each of the agents in charge of receive and broadcast messages.
 */
public class AeronAgent implements Agent {
    private static final Logger log = LoggerFactory.getLogger(AeronAgent.class);
    private final Subscription subscription;
    private final ExclusivePublication publication;
    private final byte[] dataInput;
    private final UnsafeBuffer bufferOutput;
    private final FragmentHandler fragmentHandler;
    private final MessageProcessor processor;
    
    public AeronAgent(final Subscription subscription,
            final ExclusivePublication publication, 
            final int dataInputSize, final MessageProcessor processor) {
        this.subscription = subscription;
        this.publication = publication;
        this.dataInput = new byte[dataInputSize];
        this.processor = processor;
        this.bufferOutput = new UnsafeBuffer(
                BufferUtil.allocateDirectAligned(256, dataInputSize));

        // Define a fragment handler to process received messages
        this.fragmentHandler = new FragmentHandler() {
            @Override
            public void onFragment(final DirectBuffer buffer, final int offset, 
                    final int length, final Header header) {
                buffer.getBytes(offset, dataInput);
                processor.process(dataInput, AeronAgent.this);
            }
        };
    }

    @Override
    public Void call() {
        final IdleStrategy idleStrategy = new BusySpinIdleStrategy();

        // Keep polling the subscription for messages and handle them
        log.info("Agent " + subscription + "/" + publication + " running");
        while (true) {
            int fragmentsRead = subscription.poll(this.fragmentHandler, 1);
            idleStrategy.idle(fragmentsRead);
        }
    }

    @Override
    public void publish(final byte[] data, final int length) {
        this.bufferOutput.wrap(data, 0, length);
        this.publication.offer(bufferOutput);
    }
}

