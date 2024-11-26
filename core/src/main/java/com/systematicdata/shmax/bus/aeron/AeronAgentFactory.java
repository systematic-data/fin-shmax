package com.systematicdata.shmax.bus.aeron;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.charset.*;
import java.nio.charset.StandardCharsets;
import org.slf4j.*;

import io.aeron.Aeron;
import io.aeron.CommonContext;
import io.aeron.ExclusivePublication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.BufferUtil;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import com.systematicdata.shmax.bus.*;

/**
 * Utility Factory of Aeron Agent objects.
 */
public final class AeronAgentFactory {
    private static final Logger log = LoggerFactory.getLogger(AeronAgentFactory.class);
    private static final AeronAgentFactory instance = new AeronAgentFactory();

    private AeronAgentFactory() { }

    public static AeronAgentFactory getInstance() {
        return instance;
    }


    /**
     * Buils agents to process the list of given streams using the list of provided
     * processors.
     * @param consumerStreamIds a list of consumer stream ids to create the agents.
     * @param publisherStreamIds a list of consumer stream ids to create the agents.
     * @param processors a list of Processor objects. If the size of the list is
     *      smaller than the list of streamIds, the last agent will share the 
     *      last element of the list of processors.
     */
    public StructuredTaskScope createAndStartupAgents(final Aeron aeron,
            final String consumer, final String publisher, final int dataSize,
            final List<Integer> consumerStreamIds, final List<Integer> publisherStreamIds, 
            final List<MessageProcessor> processors) {
        final StructuredTaskScope scope = new StructuredTaskScope.ShutdownOnFailure();

        final List<Agent> agents = new ArrayList<>();

        for(int i=0; i<consumerStreamIds.size(); i++) {
            final int istream = consumerStreamIds.get(i);
            final MessageProcessor processor = i<processors.size() 
                        ? processors.get(i) : processors.getLast();
            final int ostream = i<publisherStreamIds.size()
                        ? publisherStreamIds.get(i) : publisherStreamIds.getLast();

            log.info("Creating agent : Subscription channel: " + consumer
                    + ", Publich channel: " + publisher
                    + ", consumer Stream Ids: " + istream 
                    + ", producer Stream Ids: " + ostream 
                    + ", buffer data size: " + dataSize + "bytes"
                    + ", procesor: " + processor);

            agents.add(new AeronAgent(
                        processor.getClass().getName() + "-" + i,
                        aeron.addSubscription(consumer, istream),
                        aeron.addExclusivePublication(publisher, ostream),
                        dataSize, processor));
        }
        for(final Agent agent : agents) {
            scope.fork(agent);
        }
        return scope;
    }

    /**
     * Buils agents to process the list of given streams using the list of provided
     * processors but that does not publish anything to the bus.
     * @param streamIds a list of stream ids to create the agents.
     * @param processors a list of Processor objects. If the size of the list is
     *      smaller than the list of streamIds, the last agent will share the 
     *      last element of the list of processors.
     */
    public StructuredTaskScope createAndStartupAgents(final Aeron aeron,
            final String consumer, final int dataSize,
            final List<Integer> streamIds, final List<MessageProcessor> processors) {
            final StructuredTaskScope scope = new StructuredTaskScope.ShutdownOnFailure();

            final List<Agent> agents = new ArrayList<>();

            for(int i=0; i<streamIds.size(); i++) {
                final int istream = streamIds.get(i);
                final MessageProcessor processor = i<processors.size() 
                        ? processors.get(i) : processors.getLast();

                log.info("Creating agent : Subscription channel: " + consumer
                    + ", (No Publicher) "
                    + ", Stream Ids: " + istream 
                    + ", buffer data size: " + dataSize + "bytes"
                    + ", procesor: " + processor);

                agents.add(new AeronAgent(
                        processor.getClass().getName() + "-" + i,
                        aeron.addSubscription(consumer, istream),
                        dataSize, processor));
            }
            for(final Agent agent : agents) {
                scope.fork(agent);
            }
            return scope;
    }




    /**
     * Yo need a new connection for each client, even in the same application.
     */
    public Aeron buildAeronConnection() {
        final MediaDriver.Context mediaDriverCtx = new MediaDriver.Context()
                .aeronDirectoryName(CommonContext.getAeronDirectoryName() + "-client")
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true)
                .threadingMode(ThreadingMode.SHARED);
        final MediaDriver mediaDriver = MediaDriver.launchEmbedded(mediaDriverCtx);

        //construct Aeron, pointing at the media driver's folder
        final Aeron.Context aeronCtx = new Aeron.Context()
                .aeronDirectoryName(mediaDriver.aeronDirectoryName());

        return Aeron.connect(aeronCtx);
    }
}
