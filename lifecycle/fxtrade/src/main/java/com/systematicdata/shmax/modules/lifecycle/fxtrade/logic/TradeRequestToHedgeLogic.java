package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.shmax.memory.TradeMemory;
import com.systematicdata.fixmath.*;

/**
 * Implements the logic to get the trade and try to hedge it.
 */
public class TradeRequestToHedgeLogic implements TradeRequestLogic {
    private static final Logger log = LoggerFactory.getLogger(
            TradeRequestToHedgeLogic.class);

    private final Hedger hedger;
    private final TradeMemory tradesToHedge;
    private Publisher publisher;

    public TradeRequestToHedgeLogic(final TradeMemory tradesToHedge, final Hedger hedger) {
        this.hedger = hedger;
        this.tradesToHedge = tradesToHedge;
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
    public void process(final TradeRequest tradeRequest) {
        final HedgeRequest hedgeRequest = this.hedger.hedge(tradeRequest);
        if(hedgeRequest != null) {
            log.info("Publishing hedge orger " + hedgeRequest);
            this.publisher.publish(hedgeRequest);
        } else {
            tradesToHedge.put(tradeRequest);
        }
    }
}
