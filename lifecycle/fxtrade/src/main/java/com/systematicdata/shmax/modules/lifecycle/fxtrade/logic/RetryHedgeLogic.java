package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.shmax.memory.*;
import com.systematicdata.fixmath.*;

/**
 * Receives RAW ticks and memorizes in internal table to hedge stored trades.
 */
public class RetryHedgeLogic implements HedgeResultLogic {
    private static final Logger log = LoggerFactory.getLogger(MemorizeFXTickLogic.class);

    private final TickMemory ticksForHedging;
    private final TradeMemory tradesToHedge;
    private final Hedger hedger;
    private Publisher publisher;

    public RetryHedgeLogic(final TickMemory ticksForHedging, final TradeMemory tradesToHedge,
            final Hedger hedger) {
        this.ticksForHedging = ticksForHedging;
        this.tradesToHedge = tradesToHedge;
        this.hedger = hedger;
    }


    @Override
    public void setPublisher(final Publisher publisher) {
        if(this.publisher==null) {
            log.info("Configuring publisher " + publisher 
                    + " for RetryHedgeLogic");
            this.publisher = publisher;
        } else {
            log.error("Cannot configure the publisher twice the same "
                + "RetryHedgeLogic.");
        }
    }


    @Override
    public void process(final HedgeResult hedgeResult) {
        log.info("At this momnt, no retry logic is implemented.");
        log.info("Ignoring result." + hedgeResult);
    }
}
