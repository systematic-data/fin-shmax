package com.systematicdata.shmax.testtools.tick_injector;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.*;

@SpringBootApplication
public class TickInjectorTestApplication {
    public static void main(final String[] args) {
        SpringApplication.run(TickInjectorTestApplication.class, args);
    }


    @Bean
    public CommandLineRunner run(KafkaTickPriceProducer producer) {
        return args -> {
            // Send a single "hello" message to Kafka
            producer.sendTickPrice("hello");
            System.out.println("Message sent successfully!");
        };
    }
}
