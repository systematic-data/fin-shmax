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
public class HedgeResult {
    /**
     * Unique id of the hedge.
     */
    private String id="";

    /**
     * Id of the tick with the price we used.
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
     * Source that has hedged the request.
     */
    private String source="";

    /**
     * Execution vwap total price (hedge).
     */
    private FixedPointDecimal totalPrice;

    /**
     * Hedged amount
     */
    private FixedPointDecimal amount;

    /**
     * Side to hit, buy (hit ask) or sell (hit bid).
     */
    private Side side;

    /**
     * Totally hedged or not hedged totally.
     */
    private boolean hedged;

    /**
     * Creation time.
     */
    private long creationTime = System.currentTimeMillis();
    

    @Override
    public String toString() {
        return "HedgeResult id=" + id + ", used itckId=" + tickId
                + ", side=" + side + " for " + product + " " + instrument + "." + source
                + ", hedgedTotalPrice=" + totalPrice + ", hedgedAmount=" + amount
                + (hedged ? " HEDGED" : " NOT hedged");
    }
}
