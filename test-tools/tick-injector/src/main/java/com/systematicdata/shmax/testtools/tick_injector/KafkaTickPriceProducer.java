package com.systematicdata.shmax.testtools.tick_injector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.systematicdata.shmax.data.TickPrice;


/**
 * PriceTick test generator.
 */
@Service
public class KafkaTickPriceProducer {
    private final KafkaTemplate<String, TickPrice> kafkaTemplate;
    private final String topicName;

    public KafkaTickPriceProducer(KafkaTemplate<String, TickPrice> kafkaTemplate,
                         @Value("${spring.kafka.topic.tickprice}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendTickPrice(final TickPrice tickPrice) {
        kafkaTemplate.send(topicName, tickPrice);
        System.out.println("Sent TickPrice message to Kafka: " + tickPrice);
    }
}
