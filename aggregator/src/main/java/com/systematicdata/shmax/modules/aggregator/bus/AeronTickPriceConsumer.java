package com.systematicdata.shmax.modules.aggregator.bus;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import lombok.extern.slf4j.Slf4j;


import io.aeron.Aeron;
import io.aeron.CommonContext;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import com.systematicdata.shmax.data.TickPrice;
import com.systematicdata.shmax.bus.serializer.TickPriceDeserializer;
import java.nio.charset.*;


/**
 * PriceTick consumer.
 */
@Slf4j
@Component
@ConditionalOnProperty(name="shmax.ggregator.aeron.use",
        havingValue="true", matchIfMissing=false)
public class AeronTickPriceConsumer implements FragmentHandler {
    private final Aeron aeron;
    private final UnsafeBuffer unsafeBuffer;
    private final Subscription subscription;
    private final TickPriceDeserializer deserializer;
    private final Thread thread;
    private final byte[] buffer;

    public AeronTickPriceConsumer(
            @Value("${shmax.aeron.server-subscribe}") String server,
            @Value("${shmax.aeron.aggregator.rawStreamId}") int streamId) {
        this.aeron = this.buildAeronConnection();
        this.unsafeBuffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64));
        this.subscription = this.aeron.addSubscription(server, streamId);
        this.deserializer = new TickPriceDeserializer();
        this.buffer = new byte[4096];
        log.info("Subscribed to server " + server + " stream=" + streamId);
        final IdleStrategy idleStrategy = new BusySpinIdleStrategy();

        this.thread = new Thread(() -> {
            while(true) {
                int fragmentsRead = this.subscription.poll(this, 1);
                idleStrategy.idle(fragmentsRead);
            }
        });
        this.thread.start();
    }


    @Override
    public void onFragment(final DirectBuffer buffer, final int offset, final int length,
                final Header header) {
        buffer.getBytes(offset, this.buffer);
        final TickPrice tickPrice = deserializer.deserialize("", this.buffer);

        tickPrice.setAggregationTime(System.currentTimeMillis());
        System.out.println("message = " + tickPrice + ", thread=" 
                + Thread.currentThread());
        System.out.println("Total Latency (ms) : " + (tickPrice.getAggregationTime()
                - tickPrice.getVenueTime()));
        System.out.println("Bus Latency (ms) : " + (tickPrice.getL0()));
    }

    //
    // PRIVATE STUFF -------------
    //


    /**
     * TODO: Move to a factory in core.
     * Yo need a new connection for each client, even in the same application.
     */
    private Aeron buildAeronConnection() {
        final MediaDriver.Context mediaDriverCtx = new MediaDriver.Context()
            .aeronDirectoryName(CommonContext.getAeronDirectoryName() + "-client")
            .dirDeleteOnStart(true)
            .dirDeleteOnShutdown(true)
            .threadingMode(ThreadingMode.SHARED);
        final MediaDriver mediaDriver = MediaDriver.launchEmbedded(mediaDriverCtx);

        //construct Aeron, pointing at the media driver's folder
        final Aeron.Context aeronCtx = new Aeron.Context()
            .aeronDirectoryName(mediaDriver.aeronDirectoryName());

        return Aeron.connect(aeronCtx);
    }

}
