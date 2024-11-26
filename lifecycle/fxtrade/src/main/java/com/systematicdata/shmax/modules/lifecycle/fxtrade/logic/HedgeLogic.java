package com.systematicdata.shmax.modules.lifecycle.fxtrade.logic;

import java.nio.*;
import java.util.*;
import org.slf4j.*;

import com.systematicdata.shmax.bus.*;
import com.systematicdata.shmax.bus.serializer.*;
import com.systematicdata.shmax.data.*;
import com.systematicdata.shmax.logic.*;
import com.systematicdata.fixmath.*;

/**
 * Receives RAW ticks and memorizes in internal table to hedge stored trades.
 */
public class HedgeLogic {
    private static final Logger log = LoggerFactory.getLogger(HedgeLogic.class);
