package com.systematicdata.shmax.bus.serializer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.util.*;

import lombok.extern.slf4j.*;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.errors.*;

import com.systematicdata.shmax.data.*;

@Slf4j
public class TickPriceSerializer implements Serializer<TickPrice> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(final String topic, final TickPrice tickPrice) {
        if (tickPrice == null){
            log.error("Null received at serializing");
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);  
            int length = serialize(tickPrice, buffer);
            baos.write(buffer.array(), 0, length);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when serializing MessageDto to byte[]");
        }
    }

    public int serialize(final TickPrice tickPrice, final ByteBuffer buffer) 
            throws SerializationException {
        try {
            tickPrice.setT0(System.currentTimeMillis());
            
            StringSerializationUtils.serializeString(tickPrice.getId(), buffer);
            StringSerializationUtils.serializeString(tickPrice.getReqId(), buffer);
            
            StringSerializationUtils.serializeString(tickPrice.getProduct(), buffer);
            StringSerializationUtils.serializeString(tickPrice.getInstrument(), buffer);
            StringSerializationUtils.serializeString(tickPrice.getType(), buffer);
            
            buffer.putLong(tickPrice.getVenueTime());    // 8 bytes
            buffer.putLong(tickPrice.getReceptionTime());// 8 bytes
            buffer.putLong(tickPrice.getAggregationTime());// 8 bytes

            buffer.putLong(tickPrice.getT0());// 8 bytes
            

            // Price elements
            buffer.putInt(tickPrice.getRungsAsk().length);
            for(int i=0; i<tickPrice.getRungsAsk().length; i++) {
                tickPrice.getRungsAsk()[i].serialize(buffer);
                tickPrice.getAsks()[i].serialize(buffer);
            }

            buffer.putInt(tickPrice.getRungsBid().length);
            for(int i=0; i<tickPrice.getRungsBid().length; i++) {
                tickPrice.getRungsBid()[i].serialize(buffer);
                tickPrice.getBids()[i].serialize(buffer);
            }

            // Transfer buffer to byte array
            return buffer.position();
        } catch (Exception e) {
            log.error("Error when serializing TickPrice to byte[]", e);
            throw new SerializationException(
                    "Error when serializing TickPrice to byte[]", e);
        }
    }


    @Override
    public void close() {
    }
}
