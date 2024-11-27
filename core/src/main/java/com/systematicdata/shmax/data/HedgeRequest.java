package com.systematicdata.shmax.data;

import java.util.Arrays;
import lombok.*;
import com.systematicdata.fixmath.*;

/**
 * Hedge order sent to a Venue.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class HedgeRequest {
    /**
     * Unique id of the hedge.
     */
    private String id="";

    /**
     * Id of the tick with the price we want to use.
     */
    private String tickId="";

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
     * Execution vwap total price (limit).
     */
    private FixedPointDecimal limitTotalPrice;

    /**
     * Amount to hedge
     */
    private FixedPointDecimal amount;


    /**
     * Side to hit, buy (hit ask) or sell (hit bid).
     */
    private Side side;


    /**
     * Creation time.
     */
    private long creationTime = System.currentTimeMillis();
    

    @Override
    public String toString() {
        return "HedgeRequest id=" + id + ", limit price total=" + limitTotalPrice 
                + ", amount=" + amount + ", side=" + side + " for " 
                + product + " " + instrument + "." + type
                + " against suggested tick " + tickId + ", created " + creationTime;
    }
}
