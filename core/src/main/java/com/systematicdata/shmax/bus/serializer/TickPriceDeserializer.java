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

            tickPrice.id(StringSerializationUtils.deserializeString(buffer));
            tickPrice.reqId(StringSerializationUtils.deserializeString(buffer));
            tickPrice.product(StringSerializationUtils.deserializeString(buffer)); 
            tickPrice.instrument(StringSerializationUtils.deserializeString(buffer));
            tickPrice.type(StringSerializationUtils.deserializeString(buffer));
 
            tickPrice.venueTime(buffer.getLong());
            tickPrice.receptionTime(buffer.getLong());
            tickPrice.aggregationTime(buffer.getLong());

            final long t0 = buffer.getLong();
            
            // Deserialize price elements
            final int numOfRungsAsk = buffer.getInt();
                    final FixedPointDecimal rungsAsk[] = new FixedPointDecimal[numOfRungsAsk];
            final FixedPointDecimal asks[] = new FixedPointDecimal[numOfRungsAsk];
            for(int i=0; i<numOfRungsAsk; i++) {
                rungsAsk[i] = FixedPointDecimal.deserialize(buffer);
                asks[i] = FixedPointDecimal.deserialize(buffer);
            }

            final int numOfRungsBid = buffer.getInt();
            final FixedPointDecimal rungsBid[] = new FixedPointDecimal[numOfRungsBid];
            final FixedPointDecimal bids[] = new FixedPointDecimal[numOfRungsBid];
            for(int i=0; i<numOfRungsBid; i++) {
                rungsBid[i] = FixedPointDecimal.deserialize(buffer);
                bids[i] = FixedPointDecimal.deserialize(buffer);
            }

            tickPrice.rungsAsk(rungsAsk);
            tickPrice.asks(asks);
            tickPrice.rungsBid(rungsBid);
            tickPrice.bids(bids);
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
