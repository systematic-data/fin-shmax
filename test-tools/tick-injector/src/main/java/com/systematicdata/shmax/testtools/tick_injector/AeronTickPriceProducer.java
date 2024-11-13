package com.systematicdata.shmax.testtools.tick_injector;

import java.io.*;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import io.aeron.Aeron;
import io.aeron.CommonContext;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.ExclusivePublication;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;

import com.systematicdata.shmax.data.TickPrice;
import com.systematicdata.shmax.bus.serializer.TickPriceSerializer;


/**
 * PriceTick test generator.
 *
 * Quick notes on Aeron:
 *
 *      Client view: one client instance attached only to one context. 
 *      The context is represented by the "Aeron" class.
 *      UnsufaeBuffer is used to transfer/receive messages with the API (the "Aeron" object)
 *      "Channel" is the URI to communicate with server (aeron:udp?ip_address:port for example)
 *      "StreamId" is a number of the stream to send/receive messages.
 */
@Slf4j
@Service
public class AeronTickPriceProducer {
    private final Aeron aeron;
    private final ExclusivePublication publication;
    private final TickPriceSerializer serializer;
    private final UnsafeBuffer unsafeBuffer;


    public AeronTickPriceProducer(@Value("${shmax.aeron.server-publish}") String server,
                @Value("${shmax.aeron.test.streamId}") int streamId) {
        this.aeron = this.buildAeronConnection();
        this.publication = this.aeron.addExclusivePublication(server, streamId);
        this.unsafeBuffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64));
        this.serializer = new TickPriceSerializer();
        log.info("Attached to publish to server " + server + ", streamId=" + streamId);
    }

    public void sendTickPrice(final TickPrice tickPrice) {
        if(publication.isConnected()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(256);
            int length = this.serializer.serialize(tickPrice, byteBuffer);
            unsafeBuffer.wrap(byteBuffer, 0, length);
            publication.offer(unsafeBuffer, 0, length);
            System.out.println("Sent TickPrice message to Aeron: " + tickPrice);
        } else {
            System.out.println("Cannot send TickPrice message to Aeron: " + tickPrice);
        }
    }



    //
    // PRIVATE STUFF -------------
    //

    /**
     * TODO: Move to a factory in core.
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
