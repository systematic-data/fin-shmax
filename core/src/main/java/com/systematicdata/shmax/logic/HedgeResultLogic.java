package com.systematicdata.shmax.logic;

import com.systematicdata.shmax.bus.Publisher;
import com.systematicdata.shmax.data.HedgeResult;

/**
 * Interface of the logic that process hedge results.
 */
public interface HedgeResultLogic {
    /**
     * Process tick price.
     */
    public void process(final HedgeResult hedgeResult);

    /**
     * Initializes the logic with the publishing ways.
     */
    public void setPublisher(final Publisher publisher);
}
