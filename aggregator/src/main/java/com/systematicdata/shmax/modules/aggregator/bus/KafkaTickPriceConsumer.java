package com.systematicdata.shmax.modules.aggregator.bus;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * PriceTick consumer.
 */
@Component
public class KafkaTickPriceConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.tickprice.raw}", 
                  groupId = "aggregator")
    public void consume(String message) {
        System.out.println("message = " + message + ", thread=" 
                + Thread.currentThread());
    }
}
