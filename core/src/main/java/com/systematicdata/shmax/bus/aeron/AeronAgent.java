package com.systematicdata.shmax.bus.aeron;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.*;
import java.nio.*;
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
 *
 * TODO: Implement a queue to let the messages and another thread to take them
 * from it. Provide two strategies for the queue full: 
 *      forget not-processed, block if queue is full
 */
public class AeronAgent implements Agent {
    private static final Logger log = LoggerFactory.getLogger(AeronAgent.class);

    private final Subscription subscription;
    private final ExclusivePublication publication;
    private final byte[] dataInput;
    private final UnsafeBuffer bufferOutput;
    private final FragmentHandler fragmentHandler;
    private final MessageProcessor processor;
    private final String id;
    

    /**
     * Creates an Agent that only publishes into Aeron Bus.
     */
    public AeronAgent(final String id,
            final ExclusivePublication publication, 
            final int dataInputMaxSize) {
        this(id, null, publication, dataInputMaxSize, null);
    }

    /**
     * Creates an Agent that only receives messages from Aeron Bus.
     */
    public AeronAgent(final String id,
            final Subscription subscription,
            final int dataInputMaxSize, final MessageProcessor processor) {
        this(id, subscription, null, dataInputMaxSize, processor);
    }



    /**
     * Creates an Agent publishes into and receives from Aeron Bus.
     */
    public AeronAgent(final String id,
            final Subscription subscription,
            final ExclusivePublication publication, 
            final int dataInputMaxSize, final MessageProcessor processor) {
        this.id = id;
        this.subscription = subscription;
        this.publication = publication;
        this.dataInput = new byte[dataInputMaxSize];
        this.processor = processor;
        this.bufferOutput = new UnsafeBuffer(
                BufferUtil.allocateDirectAligned(256, dataInputMaxSize));

        // Define a fragment handler to process received messages
        if(processor!=null) {
            this.processor.setAgent(this);
            this.fragmentHandler = new FragmentHandler() {
                @Override
                public void onFragment(final DirectBuffer buffer, final int offset, 
                        final int length, final Header header) {
                    final long nano0 = System.nanoTime();
                    buffer.getBytes(offset, dataInput);
                    processor.process(dataInput);
                    final long nano1 = System.nanoTime();
                    //latencyReport.report(id, nano1-nano0);
                }
            };
        } else {
            this.fragmentHandler = null;
        }
    }

    @Override
    public Void call() {
        if(this.subscription == null) {
            log.error("Agent " + id + " without subscription cannot receive "
                    + " messages from Aeron bus");
        } else {
            final IdleStrategy idleStrategy = new BusySpinIdleStrategy();

            // Keep polling the subscription for messages and handle them
            log.info("Agent " + this.id + ", " 
                    + subscription + "/" 
                    + (publication!=null ? publication : "(no publication)") 
                    + " running");
            while (true) {
                int fragmentsRead = subscription.poll(this.fragmentHandler, 1);
                idleStrategy.idle(fragmentsRead);
            }
        }
        return null;
    }

    @Override
    public void publish(final ByteBuffer data, final int length) {
        if(this.publication == null) {
            log.error("Trying to publish in Agent " + this.id 
                + " without publiction");
            return;
        }
        this.bufferOutput.wrap(data, 0, length);
        this.publication.offer(bufferOutput);
    }
}

