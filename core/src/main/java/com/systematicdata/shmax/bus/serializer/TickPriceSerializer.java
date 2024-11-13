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
            ByteBuffer buffer = ByteBuffer.allocate(256);  
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
            
            buffer.putLong(tickPrice.getId());      // 8 bytes
            
            // Serialize `product` as UTF-8 bytes with length prefix 
            byte[] productBytes = tickPrice.getProduct().getBytes(StandardCharsets.UTF_8);
            buffer.putShort((short) productBytes.length); // 2 bytes for length
            buffer.put(productBytes); // UTF-8 string
            
            byte[] sourceBytes = tickPrice.getSource().getBytes(StandardCharsets.UTF_8);
            buffer.putShort((short) sourceBytes.length);  // 2 bytes for lebgth
            buffer.put(sourceBytes); // UTF-8 String
            
            buffer.putLong(tickPrice.getVenueTime());    // 8 bytes
            buffer.putLong(tickPrice.getReceptionTime());// 8 bytes
            buffer.putLong(tickPrice.getAggregationTime());// 8 bytes

            buffer.putLong(tickPrice.getT0());// 8 bytes
            
            // Serialize `price` (as BigDecimal value)
            tickPrice.getPrice().serialize(buffer);

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
