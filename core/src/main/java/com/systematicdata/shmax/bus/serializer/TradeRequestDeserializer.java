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
public class TradeRequestDeserializer implements Deserializer<TradeRequest> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public TradeRequest deserialize(final String topic, final byte[] data) {
        return this.deserialize(data);
    }


    public TradeRequest deserialize(final byte[] data) {
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



    public TradeRequest deserialize(final ByteBuffer buffer) {
        try {
            final TradeRequest.TradeRequestBuilder tradeRequest = TradeRequest.builder();

            tradeRequest.id(StringSerializationUtils.deserializeString(buffer));
            tradeRequest.product(StringSerializationUtils.deserializeString(buffer)); 
            tradeRequest.instrument(StringSerializationUtils.deserializeString(buffer));
            tradeRequest.amount(FixedPointDecimal.deserialize(buffer));
            tradeRequest.totalPrice(FixedPointDecimal.deserialize(buffer));
            tradeRequest.side(buffer.getChar() == 'B' ? Side.BUY : Side.SELL);
            tradeRequest.creationTime(buffer.getLong());

            return tradeRequest.build();
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
