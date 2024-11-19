package com.systematicdata.shmax.data;

import java.util.Arrays;
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
     * Instrument this price is referred to.
     */
    private String instrument;

    /**
     * Type of this price that originated it, or the source.
     */
    private String type;

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
     * Internal usage to measure bus latency. 
     * Only T0 serialiazed. L0 = deserialization time - T0
     */
    private long t0, l0;

    /**
     * The tick price itself.
     */
    private int numOfRungs;
    private long rungs[];
    private FixedPointDecimal bids[];
    private FixedPointDecimal asks[];


    public TickPrice cloneNoPrice() { 
        final TickPrice tickPrice = new TickPrice();
        tickPrice.setId(this.id);
        tickPrice.setProduct(this.product);
        tickPrice.setInstrument(this.instrument);
        tickPrice.setType(this.type);
        tickPrice.setVenueTime(this.venueTime);
        tickPrice.setReceptionTime(this.receptionTime);
        tickPrice.setAggregationTime(aggregationTime);
        tickPrice.setT0(this.t0);
        tickPrice.setL0(this.l0);
        return tickPrice; 
    }

    @Override
    public String toString() {
        return "tick(id="+ id + ",product=" + product 
                + ",instrument=" + instrument
                + ",type=" + type + ",venueTime=" + venueTime
                + ",receptionTime=" + receptionTime
                + ",aggregationTime=" + aggregationTime
                + ",rungs=" + Arrays.toString(rungs)
                + ",bids=" + Arrays.toString(bids)
                + ",asks=" + Arrays.toString(asks);
    }
}
