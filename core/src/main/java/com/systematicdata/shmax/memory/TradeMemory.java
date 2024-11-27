package com.systematicdata.shmax.memory;

import java.util.*;
import com.systematicdata.shmax.data.TradeRequest;


/**
 * Memory of ticks for concurrent access.
 */
public class TradeMemory {
    /**
     * Takes the trade request and let's in the memory without blocking write operations.
     */
    public List<TradeRequest> get(final String product, final String instrument) {
        return null;
    }

    /**
     * Takes the trade request and removes totally from memory.
     * It locks other takeOut and write operations.
     */
    public TradeRequest takeOut(final String tradeId) {
        return null;
    }


    /**
     * Writes locking the takeout and other puts the trade.
     */
    public void put(final TradeRequest trade) {
    }
}
