package com.systematicdata.shmax.logic;

import com.systematicdata.shmax.bus.Publisher;
import com.systematicdata.shmax.data.TradeRequest;

/**
 * Interface of the logic that process trade requests.
 */
public interface TradeRequestLogic {
    /**
     * Process trade order.
     */
    public void process(final TradeRequest trade);

    /**
     * Initializes the logic with the publishing ways.
     */
    public void setPublisher(final Publisher publisher);
}
