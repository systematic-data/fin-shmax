package com.systematicdata.shmax.bus;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.MessageProcessor;
import com.systematicdata.shmax.bus.Agent;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;

/**
 * Publisher interface for a result of a logic.
 */
public class SimpleTickPricePublisher implements Publisher {
    private static final Logger log = LoggerFactory.getLogger(
                SimpleTickPricePublisher.class);
    private Agent agent;
    private final ByteBuffer byteBuffer;
    private final TickPriceSerializer serializer;

    public SimpleTickPricePublisher() {
        this.byteBuffer = ByteBuffer.allocate(256);
        this.serializer = new TickPriceSerializer();
    }


    /**
     * Sends the data to the bus.
     * @param data mandatory a TickPrice object.
     */
    @Override
    public void publish(final Object data) {
        if(this.agent==null) {
            log.error("Trying to publish before complete initialization. Not 'Agent' set");
            return;
        }
        final TickPrice tickPrice = (TickPrice) data;
        // Serializes TickPrice
        int length = this.serializer.serialize(tickPrice, this.byteBuffer);
        this.agent.publish(this.byteBuffer, length);
    }

    public void setAgent(final Agent agent) { 
        if(this.agent==null) {
            log.info("Setting agent " + agent + " to SimpleTickPricePublisher");
            this.agent = agent; 
        } else {
            log.error("Cannot configure twice the same SimpleTickPricePublisher with an agent");
        }
    }
}
