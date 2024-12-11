package com.systematicdata.shmax.modules.webconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.systematicdata.shmax.modules.webconnector.logic.PriceBroadcaster;

@SpringBootApplication
public class WebPricesModule {

    public static void main(String[] args) {
        SpringApplication.run(WebPricesModule.class, args);
    }
}

@RestController
class WebTransportController {
    private final PriceBroadcaster priceBroadcaster;

    @Autowired
    public WebTransportController(PriceBroadcaster priceBroadcaster) {
        this.priceBroadcaster = priceBroadcaster;
    }

    @GetMapping("/prices/stream")
    public SseEmitter streamPrices() {
        SseEmitter emitter = new SseEmitter();
        priceBroadcaster.addEmitter(emitter);
        return emitter;
    }
}
