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
 * Process a message, as an array of bytes.
 * It's executed in the thread of taking the message. So it must end before the next message
 * arrives.
 */
public interface MessageProcessor {
    /**
     * Process a message and uses the Agent to send out a response.
     */
    public void process(final byte[] data, final Agent agent);
}

