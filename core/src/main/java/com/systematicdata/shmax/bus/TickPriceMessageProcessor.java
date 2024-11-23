package com.systematicdata.shmax.bus;

import java.util.*;
import java.nio.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.MessageProcessor;
import com.systematicdata.shmax.bus.Agent;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.shmax.data.*;

/**
 * Process a tick and 
 * create the aggregated price.
 */
public class TickPriceMessageProcessor implements MessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(
            TickPriceMessageProcessor.class);

    private final TickPriceDeserializer deserializer;
    private final ByteBuffer byteBuffer;
    private final TickLogic logic;
    private final Publisher publisher;


    public TickPriceMessageProcessor(final TickLogic logic,
                final Publisher publisher) {
        this.logic = logic;
        this.publisher = publisher;

        this.deserializer = new TickPriceDeserializer();

        this.byteBuffer  = ByteBuffer.allocate(4096);
        this.logic.setPublisher(publisher);

        log.info("Initialized TickPriceMessageProcessor for logic " + logic
                + " with publisher " + publisher);
    }


    public void process(final byte[] data) {
        // Deserializes TickPrice
        try {
            final TickPrice tickPriceIn = this.deserializer.deserialize(data);

            this.logic.process(tickPriceIn);
        } catch(Exception e) {
            log.error("Error deserializing data", e);
        }

    }


    @Override
    public void setAgent(final Agent agent) {
        this.publisher.setAgent(agent);
    }
}
