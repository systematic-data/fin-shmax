package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.fixmath.*;

/**
 * The lifecycle of an executed trade is just to be hedged, of it's not possible
 * to maintain open until it can be hedged inside by another opposite trade.
 */
@Slf4j
public class FxTradePositionLogic implements TickLogic, HedgeResultLogic {
    public FxTradePositionLogic() {
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
        // Maintains internal cache of RAW prices to Hedge
    }

    @Override
    public void process(final HedgeResult tradeResultIn) {
    }


    /**
     * Registers a new trade and starts to maintain the lifecycle.
     *
     * As time is critical, tries to hedge immediately.
     */
    public String registerNewTrade(final TradeData trade) {
    }
}
