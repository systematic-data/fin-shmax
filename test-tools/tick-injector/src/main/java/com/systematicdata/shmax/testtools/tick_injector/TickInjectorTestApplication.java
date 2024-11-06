package com.systematicdata.shmax.testtools.tick_injector;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.*;

import com.systematicdata.fixmath.FixedPointDecimal;
import com.systematicdata.shmax.data.TickPrice;


@SpringBootApplication
public class TickInjectorTestApplication {
    public static void main(final String[] args) {
        SpringApplication.run(TickInjectorTestApplication.class, args);
    }


    @Bean
    public CommandLineRunner run(KafkaTickPriceProducer producer) {
        return args -> {
            // Send a single "hello" message to Kafka
            for(;;) {
                final TickPrice tick = TickPrice.builder().id(System.nanoTime())
                    .product("EURUSD.SPOT").source("MT4").venueTime(System.currentTimeMillis())
                    .price(new FixedPointDecimal(1,203)).build();
                producer.sendTickPrice(tick);
                System.out.println("Message sent successfully!");
                try { Thread.sleep(1000); } catch(InterruptedException ie) {}
            }
        };
    }
}
