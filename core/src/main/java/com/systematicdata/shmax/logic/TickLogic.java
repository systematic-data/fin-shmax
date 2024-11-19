package com.systematicdata.shmax.logic;

import com.systematicdata.shmax.bus.Publisher;
import com.systematicdata.shmax.data.TickPrice;

/**
 * Interface of the logic that process tick prices and maybe publishes something.
 */
public interface TickLogic {
    /**
     * Process tick price.
     */
    public void process(final TickPrice tickPriceIn);

    /**
     * Initializes the logic with the publishing ways.
     */
    public void setPublisher(final Publisher publisher);
}
