package com.systematicdata.shmax.bus;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.Agent;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;

/**
 * Basic Publisher interface for a result of a logic.
 */
public abstract class BasicPublisher implements Publisher {
    private static final Logger log = LoggerFactory.getLogger(BasicPublisher.class);

    private Agent agent;
    protected final ByteBuffer byteBuffer;

    public BasicPublisher() {
        this.byteBuffer = ByteBuffer.allocate(4096);
        this.agent = Agent.NullAgent;
    }

    @Override
    public void setAgent(final Agent agent) {
        if(this.agent==null) {
            log.info("Setting agent " + agent + " to SimpleTickPricePublisher");
            this.agent = agent;
        } else {
            log.error("Cannot configure twice the same SimpleTickPricePublisher with an agent");
        }
    }

    public Agent getAgent() { return this.agent; }
}
