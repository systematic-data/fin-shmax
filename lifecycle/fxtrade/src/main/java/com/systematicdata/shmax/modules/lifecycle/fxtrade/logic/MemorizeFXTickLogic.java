package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.shmax.memory.TickMemory;
import com.systematicdata.fixmath.*;

/**
 * Receives RAW ticks and memorizes in internal table to hedge stored trades.
 */
    
public class MemorizeFXTickLogic implements TickLogic {
    private static final Logger log = LoggerFactory.getLogger(MemorizeFXTickLogic.class);

    private final TickMemory ticksForHedging;
    private final Hedger hedger;
    private Publisher publisher;

    public MemorizeFXTickLogic(final TickMemory ticksForHedging, final Hedger hedger) {
        this.ticksForHedging = ticksForHedging;
        this.hedger = hedger;
    }


    @Override
    public void setPublisher(final Publisher publisher) {
        if(this.publisher==null) {
            log.info("Configuring publisher " + publisher 
                    + " for FxTradePositionLogic");
            this.publisher = publisher;
        } else {
            log.error("Cannot configure the publisher twice the same "
                + "FxTradePositionLogic.");
        }
    }


    @Override
    public void process(final TickPrice tickPriceIn) {
        if( ! "FXSPOT".equals(tickPriceIn.getProduct()) ) return;

        final HedgeRequest hedgeRequest = this.hedger.hedge(tickPriceIn);
        if(hedgeRequest != null) {
            log.info("Publishing hedge orger " + hedgeRequest);
            this.publisher.publish(hedgeRequest);
        } else {
            this.ticksForHedging.put(tickPriceIn);
        }
    }
}
