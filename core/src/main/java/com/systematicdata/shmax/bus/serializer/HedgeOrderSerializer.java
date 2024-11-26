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
public class HedgeOrderSerializer implements Serializer<HedgeOrder> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(final String topic, final HedgeOrder hedgeOrder) {
        if (hedgeOrder== null){
            log.error("Null received at serializing");
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);  
            int length = serialize(hedgeOrder, buffer);
            baos.write(buffer.array(), 0, length);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when serializing MessageDto to byte[]");
        }
    }

    public int serialize(final HedgeOrder hedgeOrder, final ByteBuffer buffer) 
            throws SerializationException {
        try {
            serializeString(hedgeOrder.getId(), buffer);
            serializeString(hedgeOrder.getTickId(), buffer);
            serializeString(hedgeOrder.getProduct(), buffer);
            serializeString(hedgeOrder.getInstrument(), buffer);
            serializeString(hedgeOrder.getType(), buffer);
            hedgeOrder.getLimitTotalPrice().serialize(buffer);
            buffer.putChar(hedgeOrder.getSide() == Side.BUY ? 'B' : 'S');
            buffer.putLong(hedgeOrder.getCreationTime());
            
            return buffer.position();
        } catch (Exception e) {
            log.error("Error when serializing HedgeOrder to byte[]", e);
            throw new SerializationException(
                    "Error when serializing HedgeOrder to byte[]", e);
        }
    }


    @Override
    public void close() {
    }


    //
    // PRIVATE STUFF ------------------------
    //
    private void serializeString(final String str, final ByteBuffer buffer) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buffer.putShort((short) bytes.length);  // 2 bytes for lebgth
        buffer.put(bytes); // UTF-8 String
    }
}
