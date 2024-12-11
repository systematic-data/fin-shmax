package com.systematicdata.shmax.modules.webconnector.logic;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;

import com.systematicdata.shmax.data.TickPrice;
import com.systematicdata.shmax.bus.Publisher;
import com.systematicdata.shmax.logic.TickLogic;

/**
 * Broadcasts received price to all internally subscribed listeners.
 */
@Slf4j
@Component
public class PriceBroadcaster implements TickLogic {
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    /**
     * Initializes the logic with the publishing ways.
     */
    public void setPublisher(final Publisher publisher) {
        log.warn("Setting Publisher to PriceBroadcaster ignored");
    }


    /**
     * Adds an emmiter to propagate each tick message.
     */
    public void addEmitter(SseEmitter emitter) {
        log.info("Adding emmiter " + emitter);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    /**
     * Process a price broadcasting to emmiters.
     */
    public void process(final TickPrice price) {
        // In favour of high performance, the usage of StringBuilder instead of Gson
        // accepting the loss in maintanability.

        final StringBuilder jsonPrice = new StringBuilder("{");
        jsonPrice.append("id:'").append(price.getId()).append("',");
        jsonPrice.append("product:'").append(price.getProduct()).append("',");
        jsonPrice.append("instrument:'").append(price.getInstrument()).append("',");
        jsonPrice.append("rungsBid:'").append(Arrays.toString(price.getRungsBid())).append("',");
        jsonPrice.append("bids:'").append(Arrays.toString(price.getBids())).append("',");
        jsonPrice.append("rungsAsk:'").append(Arrays.toString(price.getRungsAsk())).append("',");
        jsonPrice.append("asks:'").append(Arrays.toString(price.getAsks())).append("'}");
        final String strPrice = jsonPrice.toString();
        for(SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(strPrice));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}

