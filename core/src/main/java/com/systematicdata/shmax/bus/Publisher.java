package com.systematicdata.shmax.bus;

import java.util.*;
import com.systematicdata.shmax.bus.MessageProcessor;
import com.systematicdata.shmax.bus.Agent;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;

/**
 * Publisher interface for a result of a logic.
 */
public interface Publisher {
    public void publish(final Object data);
    public void setAgent(final Agent agent);
}
