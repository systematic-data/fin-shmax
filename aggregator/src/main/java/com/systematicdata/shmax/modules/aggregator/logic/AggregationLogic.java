package com.systematicdata.shmax.modules.aggregator.logic;

import java.nio.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.fixmath.*;

/**
 * Process instrument and products of same type with different sources to
 * create the aggregated price.
 */
 @Slf4j
public class AggregationLogic implements TickLogic {
    private final TickPriceSerializer serializer;
    private final Map<String, Map<String, TickPrice>> instrumentSources;
    
    private Publisher publisher;


    public AggregationLogic() {
        this.instrumentSources = new HashMap<>();
        this.serializer = new TickPriceSerializer();
    }


    @Override
    public void setPublisher(final Publisher publisher) {
        if(this.publisher==null) {
            log.info("Configuring publisher " + publisher + " for AggregationLogic");
            this.publisher = publisher;
        } else {
            log.error("Cannot configure the publisher twice the same AggregationLogic.");
        }
    }


    @Override
    public void process(final TickPrice tickPriceIn) {
        // Normalize rungs: TODO
        //
        //      Same rungs for bid and ask
        //      Standard configured rungs
        //
        final FixedPointDecimal rungsAsk[] = tickPriceIn.getRungsAsk();
        final FixedPointDecimal rungsBid[] = tickPriceIn.getRungsBid();

        // Find new best price in each rung
        final Map<String, TickPrice> sources = this.instrumentSources.computeIfAbsent(
                        tickPriceIn.getInstrument(), 
                        k -> new HashMap<String, TickPrice>());
        sources.put(tickPriceIn.getType(), tickPriceIn);

        final TickPrice tickPriceOut = tickPriceIn.cloneNoPrice();
        tickPriceOut.setRungsAsk(rungsAsk);
        tickPriceOut.setRungsBid(rungsBid);
        tickPriceOut.setBids(new FixedPointDecimal[tickPriceOut.getRungsBid().length]);
        tickPriceOut.setAsks(new FixedPointDecimal[tickPriceOut.getRungsAsk().length]);

        for(int i=0; i<tickPriceIn.getRungsBid().length; i++) {
            FixedPointDecimal maxBid = FixedPointDecimal.MIN_VALUE;
            FixedPointDecimal minAsk = FixedPointDecimal.MAX_VALUE;;
            for(final TickPrice tick : sources.values()) {
                if(tick.getBids()[i].greaterThan(maxBid)) {
                    maxBid = tick.getBids()[i];
                }
                if(tick.getAsks()[i].smallerThan(minAsk)) {
                    minAsk = tick.getAsks()[i];
                }
            }
            if(maxBid.smallerThan(minAsk)) {
                // Best price is ok
                tickPriceOut.getAsks()[i] = minAsk;
                tickPriceOut.getBids()[i] = maxBid;
            } else {
                // Best price is inverted
                tickPriceOut.getAsks()[i] = maxBid;
                tickPriceOut.getBids()[i] = minAsk;
            }
        }

        // Publishes tick price.   
        this.publisher.publish(tickPriceOut);
    }
}
