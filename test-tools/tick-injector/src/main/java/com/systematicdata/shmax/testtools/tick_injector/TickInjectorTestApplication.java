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
    public CommandLineRunner run(KafkaTickPriceProducer kafkaProducer,
            AeronTickPriceProducer aeronProducer) {
        return args -> {
            if("kafka".equals(args[0])) {
                // Send a single "hello" message to Kafka
                for(;;) {
                    final TickPrice tick = TickPrice.builder().id(System.nanoTime())
                        .product("EURUSD.SPOT").type("MT4").venueTime(
                                System.currentTimeMillis())
                        .price(new FixedPointDecimal(1,203)).build();
                    kafkaProducer.sendTickPrice(tick);
                    System.out.println("Message sent successfully!");
                    try { Thread.sleep(1000); } catch(InterruptedException ie) {}
                }
            } else if("aeron".equals(args[0])) {
                // Todo, convert in Sping Boot style
                for(;;) {
                    final TickPrice tick = TickPrice.builder().id(System.nanoTime())
                        .product("EURUSD.SPOT").type("MT4").venueTime(
                                System.currentTimeMillis())
                        .price(new FixedPointDecimal(1,203)).build();
                    aeronProducer.sendTickPrice(tick);
                    System.out.println("Message sent successfully!");
                    try { Thread.sleep(1000); } catch(InterruptedException ie) {}
                }
            } else {
                System.out.println("Usage:");
                System.out.println();
                System.out.print("\t\tUse with argument 'kafka'|'aeron' depending ");
                System.out.println("the bus you want to use.");
                System.out.println();
            }
        };
    }
}
