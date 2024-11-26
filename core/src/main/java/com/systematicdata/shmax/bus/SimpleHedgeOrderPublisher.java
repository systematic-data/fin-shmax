package com.systematicdata.shmax.bus;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.MessageProcessor;
import com.systematicdata.shmax.bus.Agent;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;

/**
 * Publisher of HedgeOrders for a result of a logic.
 */
public class SimpleHedgeOrderPublisher extends BasicPublisher {
    private static final Logger log = LoggerFactory.getLogger(
                SimpleHedgeOrderPublisher.class);
    private final HedgeOrderSerializer serializer;

    public SimpleHedgeOrderPublisher() {
        this.serializer = new HedgeOrderSerializer();
    }


    /**
     * Sends the data to the bus.
     * @param data mandatory a TickPrice object.
     */
    @Override
    public void publish(final Object data) {
        if(this.getAgent()==null) {
            log.error("Trying to publish before complete initialization. Not 'Agent' set");
            return;
        }
        final HedgeOrder order = (HedgeOrder) data;
        // Serializes TickPrice
        this.byteBuffer.rewind();
        int length = this.serializer.serialize(order, this.byteBuffer);
        this.getAgent().publish(this.byteBuffer, length);
    }
}
