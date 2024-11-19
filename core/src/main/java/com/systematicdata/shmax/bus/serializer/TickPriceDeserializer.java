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
            
            // Deserialize 'product' as UTF-8 bytes with length prefix 
            final int lengthProduct = buffer.getShort();
            final byte[] bytesProduct = new byte[lengthProduct];
            buffer.get(bytesProduct);
            tickPrice.product(new String(bytesProduct, StandardCharsets.UTF_8));

            // Deserialize 'instrument' as UTF-8 bytes with length prefix 
            final int lengthInstrument = buffer.getShort();
            final byte[] bytesInstrument = new byte[lengthInstrument];
            buffer.get(bytesInstrument);
            tickPrice.instrument(new String(bytesInstrument, StandardCharsets.UTF_8));
 
            // Deserilize 'type' as UTF-8 with length prefix
            final int lengthType = buffer.getShort();
            final byte[] bytesType = new byte[lengthType];
            buffer.get(bytesType);
            tickPrice.type(new String(bytesType, StandardCharsets.UTF_8));
 
            tickPrice.venueTime(buffer.getLong());
            tickPrice.receptionTime(buffer.getLong());
            tickPrice.aggregationTime(buffer.getLong());

            final long t0 = buffer.getLong();
            tickPrice.t0(t0);
            
            // Deserialize price elements
            final int numOfRungs = buffer.getInt();
            final long rungs[] = new long[numOfRungs];
            final FixedPointDecimal bids[] = new FixedPointDecimal[numOfRungs];
            final FixedPointDecimal asks[] = new FixedPointDecimal[numOfRungs];
            for(int i=0; i<numOfRungs; i++) {
                rungs[i] = buffer.getLong(); 
                bids[i] = FixedPointDecimal.deserialize(buffer);
                asks[i] = FixedPointDecimal.deserialize(buffer);
            }
            tickPrice.rungs(rungs);
            tickPrice.bids(bids);
            tickPrice.asks(asks);
            
            tickPrice.l0(System.currentTimeMillis() - t0);
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
