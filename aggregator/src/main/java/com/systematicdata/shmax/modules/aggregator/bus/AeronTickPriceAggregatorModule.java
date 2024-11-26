package com.systematicdata.shmax.modules.aggregator.bus;

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
import com.systematicdata.shmax.modules.aggregator.*;
import com.systematicdata.shmax.modules.aggregator.logic.*;


/**
 * PriceTick consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.ggregator.aeron.use",
                       havingValue="true", matchIfMissing=false)
public class AeronTickPriceAggregatorModule {
    private final Aeron aeron;

    public AeronTickPriceAggregatorModule(
            @Value("${shmax.aeron.aggregator.server-subscribe}") String consumer,
            @Value("${shmax.aeron.aggregator.server-publish}") String publisher,
            @Value("${shmax.aeron.aggregator.dataSize}") int dataSize,
            @Value("${shmax.aeron.aggregator.rawStreamIds}") String subscribeStreamIds,
            @Value("${shmax.aeron.aggregator.aggStreamIds}") String publishStreamIds) {
        final List<Integer> istreamIds = Arrays.stream(subscribeStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> ostreamIds = Arrays.stream(publishStreamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());

        final List<MessageProcessor> processors = new ArrayList<>();
        processors.add(new TickPriceMessageProcessor(
                new BestPriceAggregationLogic(),
                new SimpleTickPricePublisher()));

        this.aeron = AeronAgentFactory.getInstance().buildAeronConnection();

        AeronAgentFactory.getInstance().createAndStartupAgents(
                this.aeron, consumer, publisher, dataSize, istreamIds, ostreamIds,
                processors);
    }
}
