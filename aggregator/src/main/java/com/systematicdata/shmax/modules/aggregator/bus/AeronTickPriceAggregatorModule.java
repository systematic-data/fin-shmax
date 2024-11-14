package com.systematicdata.shmax.modules.aggregator.bus;

import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.*;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import lombok.extern.slf4j.Slf4j;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import com.systematicdata.shmax.data.TickPrice;
import com.systematicdata.shmax.bus.serializer.TickPriceDeserializer;
import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.aeron.*;


/**
 * PriceTick consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.ggregator.aeron.use",
        havingValue="true", matchIfMissing=false)
public class AeronTickPriceAggregatorModule {
    private final Aeron aeron;
    private final TickPriceDeserializer deserializer;

    public AeronTickPriceAggregatorModule(
            @Value("${shmax.aeron.aggregator.server-subscribe}") String consumer,
            @Value("${shmax.aeron.aggregator.server-publish}") String publisher,
            @Value("${shmax.aeron.aggregator.dataSize}") int dataSize,
            @Value("${shmax.aeron.aggregator.rawStreamIds}") String streamIds) {
        this.deserializer = new TickPriceDeserializer();
        final List<Integer> istreamIds = Arrays.stream(streamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<MessageProcessor> processors = new ArrayList<>();
        processors.add((data, agent) -> {
                final TickPrice tickPrice = deserializer.deserialize(data);

                tickPrice.setAggregationTime(System.currentTimeMillis());
                System.out.println("message = " + tickPrice + ", thread=" 
                        + Thread.currentThread());
                System.out.println("Total Latency (ms) : " + (tickPrice.getAggregationTime()
                        - tickPrice.getVenueTime()));
                System.out.println("Bus Latency (ms) : " + (tickPrice.getL0()));
            });

        this.aeron = AeronAgentFactory.getInstance().buildAeronConnection();
        AeronAgentFactory.getInstance().createAndStartupAgents(
                this.aeron, consumer, publisher, dataSize, istreamIds, processors);
    }
}
