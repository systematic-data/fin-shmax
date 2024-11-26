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
public class TradeOrder {
    /**
     * Unique id order
     */
    private String id="";

    /**
     * Product this price is referred to.
     */
    private String product="";

    /**
     * Instrument this price is referred to.
     */
    private String instrument="";

    /**
     * Execution volume.
     */
    private FixedPointDecimal amount;

    /**
     * Execution limit price.
     */
    private FixedPointDecimal totalPrice;

    /**
     * Side to hit, buy (hit ask) or sell (hit bid).
     */
    private Side side;

    /**
     * Creation time.
     */
    private final long creationTime = System.currentTimeMillis();
    

    @Override
    public String toString() {
        return "TradeOrder id=" + id + ", side=" + side
                + "amount=" + amount + ", totalLimitPrice=" + totalPrice 
                + " " + product + " " + instrument + ", created " + creationTime;
    }
}
