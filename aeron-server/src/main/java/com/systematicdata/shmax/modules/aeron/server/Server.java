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
 * Main entry point of Servers
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        final String serverName = args[0];
        // Todo: add help on how to use it.

        switch(serverName.toUpperCase()) {
            case "BRIDGE":
                new Bridge(Arrays.copyOfRange(args,1, args.length));
                break;
            case "TEST":
                System.out.println("Testing parameter. No usage");
                break;
            default:
                System.out.println("Useage:");
                System.out.println("\t\tServer bridge|test ARGS");
                System.out.println();
                System.out.println("Where:");
                System.out.println("\tARGS: Specific arguments of the selected server");
        }
    }
}
