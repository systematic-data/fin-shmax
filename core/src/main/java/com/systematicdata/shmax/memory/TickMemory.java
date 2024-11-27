package com.systematicdata.shmax.memory;

import java.util.*;
import com.systematicdata.shmax.data.TickPrice;

/**
 * Memory of ticks for concurrent access.
 */
public class TickMemory {
    /**
     * Gets a tick without locking write operations.
     */
    public List<TickPrice> get(final String product, final String instrument) {
        return null;
    }

    /**
     * Takes and removes totally the tick.
     * It locks other takeOut and write operations.
     * @return null if no tick found, or the request tick if it's present.
     */
    public TickPrice takeOut(final String tickId) {
        return null;
    }


    /**
     * Writes (locking other put and the takeOut) the tick into memory.
     */
    public void put(final TickPrice tick) {
    }
}
