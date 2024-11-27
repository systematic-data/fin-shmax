package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.shmax.memory.*;
import com.systematicdata.fixmath.*;
import static com.systematicdata.fixmath.FixedPointDecimal.POSITIVE;
import static com.systematicdata.fixmath.FixedPointDecimal.NEGATIVE;

/**
 * Hedge logic that tries to hedge received trades and to use received ticks to
 * hedges against stored trades.
 * It sends out HedgeRequests and removes the used ticks from internal available memory of
 * ticks.
 */
public class Hedger {
    private static final Logger log = LoggerFactory.getLogger(Hedger.class);

    private final TickMemory ticksForHedging;
    private final TradeMemory tradesToHedge;

    /**
     * Creates the hedging logic.
     */
    public Hedger(final TickMemory ticksForHedging, 
            final TradeMemory tradesToHedge) {
        this.tradesToHedge = tradesToHedge;
        this.ticksForHedging = ticksForHedging;
    }


    /**
     * Try to use the new tick to hedge one (randomly) of the stored trades.
     * @return true if the tick could be used to hedge a previous trade. False otherwise.
     */
    public HedgeRequest hedge(final TickPrice tick) {
        //log.info("Trying to hedge pending trade using " + tick);
        String selectedTradeId = null;
        FixedPointDecimal selectedTotalPrice = null;

        // No order in trade selection, random order.
        for(final TradeRequest trade : 
                this.tradesToHedge.get(tick.getProduct(), tick.getInstrument())) {
            final FixedPointDecimal price = canHedge(trade, tick);
            if(price!=null) {
                selectedTradeId = trade.getId();
                selectedTotalPrice = price;
                break;
            }
        }

        // Retake selected trade from memory, to avoid other threads to do it
        if(selectedTradeId != null) {
            final TradeRequest trade = this.tradesToHedge.takeOut(selectedTradeId);
            if(trade == null) return null;
            final HedgeRequest hedgeRequest = createHedge(trade, tick, selectedTotalPrice);
            return hedgeRequest;
        } else {
            //log.info("Cannot use tick " + tick.getId() + " for hedging");
            return null;
        }
    }




    /**
     * Try to solve internal trades, avoiding to go to venues.
     */
    public void innerHedge() {
        log.info("At this point, no inner hedge is available...");  
    }


    /**
     * Try to hedge the new trade with the stored ticks.
     * @return true if the trade was tried to be hedged, false otherwise.
     */
    public HedgeRequest hedge(final TradeRequest trade) {
        log.info("Trying to hedge trade " + trade + " using stored ticks");
        String bestTickId = null;
        FixedPointDecimal bestPrice = null;
        // No order in trade selection, random order.
        for(final TickPrice tick : 
                this.ticksForHedging.get(trade.getProduct(), trade.getInstrument())) {
            final FixedPointDecimal price = canHedge(trade, tick);
            if(price!=null) {
                if(trade.getSide()==Side.BUY) {
                    if(bestPrice==null || price.greaterThan(bestPrice)) {
                        bestPrice = price;
                        bestTickId = tick.getId();
                    }
                } else {
                    if(bestPrice==null || price.smallerThan(bestPrice)) {
                        bestPrice = price;
                        bestTickId = tick.getId();
                    }
                }
            }
        }

        if(bestPrice!=null) {
            if(trade.getSide()==Side.BUY) {
                if(bestPrice.greaterThan(trade.getTotalPrice())) {
                    log.info("Can hedge trade " + trade.getId() + " with tick id " + bestTickId);
                    // Ok, we can hedge
                } else {
                    log.info("Cannot hedge at this moment the trade " + trade.getId());
                    return null;
                }
            } else {
                if(bestPrice.smallerThan(trade.getTotalPrice())) {
                    log.info("Can hedge trade " + trade.getId() + " with tick id " + bestTickId);
                    // Ok, we can hedge
                } else {
                    log.info("Cannot hedge at this moment the trade " + trade.getId());
                    return null;
                }
            }
        }

        final TickPrice tick = this.ticksForHedging.takeOut(bestTickId);
        if(tick==null) return null;
        final HedgeRequest hedgeRequest = createHedge(trade, tick, bestPrice);
        return hedgeRequest;
    }


    //
    // Private SUFF ------------------------------
    //

    /**
     * Checks if the trade can be hedged totally with the tick.
     * @return the price that can be achieved with this tick or null if trade cannot be hedge.
     */
    protected FixedPointDecimal canHedge(final TradeRequest trade, final TickPrice tick) {
        final FixedPointDecimal pricesTick[];
        final FixedPointDecimal volumesTick[];
        if(trade.getSide()==Side.BUY) {
            // Hit the ask
            pricesTick = tick.getAsks();
            volumesTick = tick.getRungsAsk();
        } else {
            // Hit the bid 
            pricesTick = tick.getBids();
            volumesTick = tick.getRungsBid();
        }

        final FixedPointDecimal remainAmount = new FixedPointDecimal(trade.getAmount());
        final FixedPointDecimal vwap = new FixedPointDecimal(0);
        for(int i=0; remainAmount.isPositive(); i++) {
            FixedPointDecimal q = volumesTick[i];
            if(q.greaterThan(remainAmount)) {
                q = remainAmount;
            }
            vwap.addTo(q.multiply(pricesTick[i]));
            remainAmount.subtract(q);
        }
        if(remainAmount.isPositive()) return null;    // Tick has not enough liquidity

        if(trade.getSide()==Side.BUY) {
            if(vwap.smallerThan(trade.getTotalPrice())) {
                return vwap;
            } else {
                return null;
            }
        } else {
            if(vwap.greaterThan(trade.getTotalPrice())) {
                return vwap;
            } else {
                return null;
            }
        }
    }



    /**
     * Creates a hedge order associated to the given trade.
     */
    protected HedgeRequest createHedge(final TradeRequest trade, final TickPrice tick, 
                final FixedPointDecimal limitTotalPrice) {
        final HedgeRequest hedgeRequest = new HedgeRequest();
        hedgeRequest.setId(System.currentTimeMillis() + "-" + trade.getId());
        hedgeRequest.setTickId(tick.getId());
        hedgeRequest.setProduct(trade.getProduct());
        hedgeRequest.setInstrument(trade.getInstrument());
        hedgeRequest.setType(tick.getType());
        hedgeRequest.setLimitTotalPrice(limitTotalPrice);
        hedgeRequest.setSide(trade.getSide());
        log.info("Created HedgeRequest : " + hedgeRequest + " for hedging trade " 
                + trade.getId() + " using tickId " + tick.getId());
        return hedgeRequest;
    }
}
