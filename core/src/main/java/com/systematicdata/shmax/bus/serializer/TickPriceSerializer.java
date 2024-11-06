package com.systematicdata.shmax.bus.serializer;

import com.systematicdata.shmax.data.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.errors.*;

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
                    // Arbitrary starting size; can expand dynamically
            
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
            
            // Serialize `price` (as BigDecimal value)
            tickPrice.getPrice().serialize(buffer);
            
            // Transfer buffer to byte array
            baos.write(buffer.array(), 0, buffer.position());
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when serializing MessageDto to byte[]");
        }
    }


    @Override
    public void close() {
    }
}
