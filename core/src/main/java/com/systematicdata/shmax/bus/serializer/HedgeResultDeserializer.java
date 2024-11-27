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
public class HedgeResultDeserializer implements Deserializer<HedgeResult> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public HedgeResult deserialize(final String topic, final byte[] data) {
        return this.deserialize(data);
    }


    public HedgeResult deserialize(final byte[] data) {
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



    public HedgeResult deserialize(final ByteBuffer buffer) {
        try {
            final HedgeResult.HedgeResultBuilder hedgeResult = HedgeResult.builder();

            hedgeResult.id(StringSerializationUtils.deserializeString(buffer));
            hedgeResult.tickId(StringSerializationUtils.deserializeString(buffer));
            hedgeResult.product(StringSerializationUtils.deserializeString(buffer)); 
            hedgeResult.instrument(StringSerializationUtils.deserializeString(buffer));
            hedgeResult.source(StringSerializationUtils.deserializeString(buffer));
            hedgeResult.totalPrice(FixedPointDecimal.deserialize(buffer));
            hedgeResult.amount(FixedPointDecimal.deserialize(buffer));
            hedgeResult.side(buffer.getChar() == 'B' ? Side.BUY : Side.SELL);
            hedgeResult.hedged(buffer.getChar() == 'T');
            hedgeResult.creationTime(buffer.getLong());

            return hedgeResult.build();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when deserializing MessageDto to byte[]", e);
        }
    }



    @Override
    public void close() {
    }


    //
    // -------------------- PRIVATE STUFF 
    //

    private String deserializeString(final ByteBuffer buffer) {
        final int length = buffer.getShort();
        final byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
