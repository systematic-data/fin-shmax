package com.systematicdata.shmax.modules.venues.fix.example;

import java.time.*;
import java.time.format.*;
import java.util.*;
import org.slf4j.*;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.*;

import com.systematicdata.shmax.modules.venues.fix.*;
import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.fixmath.*;


public class FixExampleConnection implements FixConnection {
    private final ApplicationImpl fixConnection;
    private final DateTimeFormatter utcTimeOnly = DateTimeFormatter.ofPattern("HH:mm:ss[.SSS]");
    private final Publisher publisher;

    public FixExampleConnection(final String configFile, final Publisher publisher) {
        this.fixConnection = new ApplicationImpl(this, configFile);
        this.publisher = publisher;
    }


    @Override
    public void init() {
        this.fixConnection.init();
    }


    @Override
    public String sendSubscriptionRequest(final String instrument) {
        // Send subscription request for EUR/USD SPOT
        MarketDataRequest request = new MarketDataRequest();
        final String reqId = Long.toString(System.nanoTime());
        request.set(new MDReqID(reqId));
        request.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_UPDATES));
        request.set(new MarketDepth(0)); // Full market depth
        
        // Add instrument details
        MarketDataRequest.NoRelatedSym symbolGroup = new MarketDataRequest.NoRelatedSym();
        symbolGroup.set(new Symbol(instrument));
        symbolGroup.set(new SecurityType(SecurityType.FX_SPOT));
        request.addGroup(symbolGroup); 
        
        // Add request types (BID, OFFER)
        request.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        request.set(new AggregatedBook(true));   

        if(this.fixConnection.sendSubscriptionRequest(request)) return reqId;
        else return null;
    }

    private static final NoMDEntries NOMDENTRIES = new NoMDEntries();
    private static final MDEntryType MDENTRYTYPE = new MDEntryType();
    private static final MDEntryPx MDENTRYPX = new MDEntryPx();
    private static final MDEntrySize MDENTRYSIZE = new MDEntrySize();
    private static final MDReqID MDREQID = new MDReqID();
    private static final QuoteID QUOTEID = new QuoteID();
    private static final Symbol SYMBOL = new Symbol();
    private static final SendingTime SENDINGTIME = new SendingTime();


    @Override
    public void onMessage(final MarketDataIncrementalRefresh message, SessionID sessionID) {
        try {
            final TickPrice tickPrice = new TickPrice();
            tickPrice.setReceptionTime(System.currentTimeMillis());

            final List<FixedPointDecimal> rungsBid = new ArrayList<>();
            final List<FixedPointDecimal> rungsAsk = new ArrayList<>();
            final List<FixedPointDecimal> bids = new ArrayList<>();
            final List<FixedPointDecimal> asks = new ArrayList<>();
            String symbol = "";
            final int numOfGroups = message.get(NOMDENTRIES).getValue();
            for(int i=1; i<=numOfGroups; i++) {
                final MarketDataIncrementalRefresh.NoMDEntries group = 
                        new MarketDataIncrementalRefresh.NoMDEntries();
                message.getGroup(i, group);
                symbol = group.getField(SYMBOL).getValue();
                final char type = group.get(MDENTRYTYPE).getValue();
                final FixedPointDecimal price = new FixedPointDecimal(
                        group.get(MDENTRYPX).getValue());

                final FixedPointDecimal size = new FixedPointDecimal(
                        group.get(MDENTRYSIZE).getValue());

                // Rungs must appear in ascendant order.
                if(type == MDEntryType.BID) {
                    bids.add(price);
                    rungsBid.add(size);
                } else {
                    asks.add(price);
                    rungsAsk.add(size);
                }
            }

            tickPrice.setId(Long.toString(System.nanoTime()));
            tickPrice.setReqId(message.getField(MDREQID).getValue());
            tickPrice.setProduct("FXSPOT");
            tickPrice.setInstrument(symbol);
            tickPrice.setType("SAMPLE");
            tickPrice.setVenueTime(message.getHeader().getField(SENDINGTIME)
                    .getValue().toInstant(ZoneOffset.UTC).toEpochMilli());
            tickPrice.setRungsAsk(rungsAsk.toArray(new FixedPointDecimal[rungsAsk.size()]));
            tickPrice.setAsks(asks.toArray(new FixedPointDecimal[asks.size()]));
            tickPrice.setRungsBid(rungsBid.toArray(new FixedPointDecimal[rungsBid.size()]));
            tickPrice.setBids(bids.toArray(new FixedPointDecimal[bids.size()]));

            publisher.publish(tickPrice);
        } catch(FieldNotFound e) {
System.out.println("ERROR");
e.printStackTrace();
//        log.error("Error parsing FIX message:", e);


        }
    }
}
