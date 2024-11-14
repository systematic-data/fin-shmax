package com.systematicdata.shmax.bus;

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
public interface Agent extends Callable<Void> {
    /**
     * Publish the message to bus.
     */
    public void publish(final byte[] data, final int length);

    /**
     * Starts-up the agent to process incoming messages.
     */
    public Void call();
}
