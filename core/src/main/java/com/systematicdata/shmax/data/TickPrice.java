package com.systematicdata.shmax.data;

import lombok.*;
import com.systematicdata.fixmath.*;

/**
 * Tick price of a product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class TickPrice {
    /**
     * Unique id for this produc and source.
     */
    private long id;

    /**
     * Product this price is referred to.
     */
    private String product;

    /**
     * Source of this price that originated it.
     */
    private String source;


    /**
     * Time when price was originated.
     */
    private long venueTime;

    /**
     * Time when price was received in the system.
     */
    private long receptionTime;

    /**
     * Time when price was received in the system.
     */
    private long aggregationTime;


    /**
     * Internal usage to measure bus latency. Only T0 serialiazed.
     */
    private long t0, l0;

    /**
     * The tick price itself.
     */
    private FixedPointDecimal price;


    @Override
    public String toString() {
        return "tick(id="+ id + ",product=" + product 
                + ",source=" + source + ",venueTime=" + venueTime
                + ",receptionTime=" + receptionTime
                + ",aggregationTime=" + aggregationTime
                + ",price=" + price + ")";
    }
}
