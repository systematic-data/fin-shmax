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
     * Unique tick id for this product and source.
     */
    private String id="";

    /**
     * Id of the request to which this tick belongs.
     */
    private String reqId="";

    /**
     * Product this price is referred to.
     */
    private String product="";

    /**
     * Instrument this price is referred to.
     */
    private String instrument="";

    /**
     * Type of this price that originated it, or the source.
     */
    private String type="";

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
    private FixedPointDecimal rungsBid[];
    private FixedPointDecimal bids[];
    private FixedPointDecimal rungsAsk[];
    private FixedPointDecimal asks[];


    public TickPrice cloneNoPrice() { 
        final TickPrice tickPrice = new TickPrice();
        tickPrice.setId(this.id);
        tickPrice.setReqId(this.reqId);
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
        return "tick(id="+ id + ",reqId=" + reqId + ",product=" + product 
                + ",instrument=" + instrument
                + ",type=" + type + ",venueTime=" + venueTime
                + ",receptionTime=" + receptionTime
                + ",aggregationTime=" + aggregationTime
                + ",rungsBid=" + Arrays.toString(rungsBid)
                + ",bids=" + Arrays.toString(bids)
                + ",rungsAsk=" + Arrays.toString(rungsAsk)
                + ",asks=" + Arrays.toString(asks);
    }
}
