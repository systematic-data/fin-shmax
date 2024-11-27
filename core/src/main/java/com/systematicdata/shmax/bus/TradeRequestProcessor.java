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
 * Process a trade request and provokes possibly an answer.
 */
public class TradeRequestProcessor implements MessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(
            TradeRequestProcessor.class);

    private final TradeRequestDeserializer deserializer;
    private final ByteBuffer byteBuffer;
    private final TradeRequestLogic logic;
    private final Publisher publisher;


    /**
     * Creates a tick processor that does not emit any answer directly.
     */
    public TradeRequestProcessor(final TradeRequestLogic logic) {
        this(logic, null);
    }
 

    /**
     * Creates a tick processor that does emit an answer directly.
     */
    public TradeRequestProcessor(final TradeRequestLogic logic,
                final Publisher publisher) {
        this.logic = logic;
        this.publisher = publisher;

        this.deserializer = new TradeRequestDeserializer();

        this.byteBuffer  = ByteBuffer.allocate(4096);
        if(publisher!=null) this.logic.setPublisher(publisher);

        log.info("Initialized TradeRequestProcessor for logic " + logic
                + (publisher==null ?  " with no publisher." 
                                   : (" with publisher " + publisher)));
    }


    public void process(final byte[] data) {
        // Deserializes TradeRequest
        try {
            final TradeRequest tradeRequest = this.deserializer.deserialize(data);

            this.logic.process(tradeRequest);
        } catch(Exception e) {
            log.error("Error deserializing data", e);
        }
    }


    @Override
    public void setAgent(final Agent agent) {
        if(this.publisher != null) {
            log.info("Setting publisher " + publisher + " to agent.");
            this.publisher.setAgent(agent);
        } else {
            log.info("No publisher publisher set to agent.");
        }
    }
}
