package com.systematicdata.shmax.modules.venues.fix.example;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import lombok.extern.slf4j.Slf4j;

import io.aeron.Aeron;
import io.aeron.Subscription;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.aeron.*;

import com.systematicdata.shmax.modules.venues.fix.*;


/**
 * PriceTick generator from venue.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.venues.fix.examples.quickfix.aeron.use",
                       havingValue="true", matchIfMissing=false)
public class QuickFIXVenueModule {
    private final Aeron aeron;
    private final AeronAgent agent;

    public QuickFIXVenueModule(
            @Value("${shmax.aeron.venues.fix.examples.quickfix.server-publish}") String publisher,
            @Value("${shmax.aeron.venues.fix.examples.quickfix.dataSize}") int dataSize,
            @Value("${shmax.aeron.venues.fix.examples.quickfix.rawStreamId}") int streamId,
            @Value("${shmax.aeron.venues.fix.examples.quickfix.configFile}") String configFile) {
        final Publisher tickPublisher = new SimpleTickPricePublisher();
        this.aeron = AeronAgentFactory.getInstance().buildAeronConnection();

        log.info("Creating Aeron Agent, publisher:" + publisher);
        log.info("Creating Aeron Agent, dataSize:" + dataSize);
        log.info("Creating Aeron Agent, rawStreamId:" + streamId);
        this.agent = new AeronAgent("QuickFIXExampleAgent", 
                this.aeron.addExclusivePublication(publisher, streamId), dataSize);
        tickPublisher.setAgent(agent);

        log.info("Creating FixExampleConnection....");
        final FixExampleConnection fixConnection = 
                new FixExampleConnection(configFile, tickPublisher);
        log.info("Created FixExampleConnection.");

        log.info("Init FixExampleConnection.");
        fixConnection.init();
        log.info("FixExampleConnection.");
    }
}
