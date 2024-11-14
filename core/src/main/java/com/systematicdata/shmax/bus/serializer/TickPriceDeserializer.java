package com.systematicdata.shmax.bus.serializer;

import com.systematicdata.shmax.data.*;
import com.systematicdata.fixmath.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.errors.*;

@Slf4j
public class TickPriceDeserializer implements Deserializer<TickPrice> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public TickPrice deserialize(final String topic, final byte[] data) {
        return this.deserialize(data);
    }


    public TickPrice deserialize(final byte[] data) {
        if (data == null){
            log.error("Null received at deserializing");
            return null;
        }
        try {
            final ByteBuffer buffer = ByteBuffer.wrap(data);
            return this.deserialize(buffer);
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when deserializing MessageDto to byte[]", e);
        }
    }


    public TickPrice deserialize(final ByteBuffer buffer) {
        try {
            final TickPrice.TickPriceBuilder tickPrice = TickPrice.builder();
            tickPrice.id(buffer.getLong());
            
            // Serialize 'product' as UTF-8 bytes with length prefix 
            final int lengthProduct = buffer.getShort();
            final byte[] bytesProduct = new byte[lengthProduct];
            buffer.get(bytesProduct);
            tickPrice.product(new String(bytesProduct, StandardCharsets.UTF_8));
            
            // Deserilize 'source' as UTF-8 with length prefix
            final int lengthSource = buffer.getShort();
            final byte[] bytesSource = new byte[lengthSource];
            buffer.get(bytesSource);
            tickPrice.source(new String(bytesSource, StandardCharsets.UTF_8));
 
            tickPrice.venueTime(buffer.getLong());
            tickPrice.receptionTime(buffer.getLong());
            tickPrice.aggregationTime(buffer.getLong());

            tickPrice.l0(System.currentTimeMillis() - buffer.getLong());
            
            // Serialize `price` (as BigDecimal value)
            tickPrice.price(FixedPointDecimal.deserialize(buffer));
            
            return tickPrice.build();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when deserializing MessageDto to byte[]", e);
        }
    }



    @Override
    public void close() {
    }
}
