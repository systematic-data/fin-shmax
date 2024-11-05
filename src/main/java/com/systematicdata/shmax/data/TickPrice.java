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
     * The tick price itself.
     */
    private FixedPointDecimal price;
}
