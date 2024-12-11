package com.systematicdata.shmax.modules.webconnector.bus;


import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import lombok.extern.slf4j.Slf4j;

import io.aeron.Aeron;
import io.aeron.Subscription;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.aeron.*;
import com.systematicdata.shmax.modules.webconnector.logic.PriceBroadcaster;

/**
 * Tradeable ticks consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.ggregator.aeron.use",
                       havingValue="true", matchIfMissing=false)
public class AeronWebPricesModule {
    private final PriceBroadcaster priceBroadcaster;

    private final Aeron aeron;

    @Autowired
    public AeronWebPricesModule(
            @Value("${shmax.aeron.webprices.server-subscribe}") String consumer,
            @Value("${shmax.aeron.webprices.dataSize}") int dataSize,
            @Value("${shmax.aeron.webprices.priceStreamIds}") String streamIds,
            final PriceBroadcaster priceBroadcaster) {
        this.priceBroadcaster = priceBroadcaster;

        final List<Integer> istreamIds = Arrays.stream(streamIds.split(","))
                .map(Integer::parseInt).collect(Collectors.toList());

        final List<MessageProcessor> processors = new ArrayList<>();

        processors.add(new TickPriceMessageProcessor(priceBroadcaster));

        this.aeron = AeronAgentFactory.getInstance().buildAeronConnection();

        AeronAgentFactory.getInstance().createAndStartupAgents(
                this.aeron, consumer, dataSize, istreamIds, processors);
    }
}
