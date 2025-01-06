package com.systematicdata.shmax.modules.venues.fix;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataIncrementalRefresh;

/**
 * Interface of the FIX connections depending on Venues.
 */
public interface FixConnection {
    public void init();


    /**
     * Subscribe to instrument.
     * @return the requestID of this subscription, null if failed.
     */
    public String sendSubscriptionRequest(final String instrument);


    /**
     * Unsubscribe to instrument.
     */
    //public boolean sendUnsubcriptionRequest(final String instrument) 

    /**
     * Process MarketDataIncrementalRefresh message.
     */
    public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionID);
}
