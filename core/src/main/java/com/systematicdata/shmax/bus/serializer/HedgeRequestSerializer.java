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
public class HedgeRequestSerializer implements Serializer<HedgeRequest> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(final String topic, final HedgeRequest hedgeRequest) {
        if (hedgeRequest== null){
            log.error("Null received at serializing");
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ByteBuffer buffer = ByteBuffer.allocate(4096);  
            int length = serialize(hedgeRequest, buffer);
            baos.write(buffer.array(), 0, length);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(
                    "Error when serializing MessageDto to byte[]");
        }
    }

    public int serialize(final HedgeRequest hedgeRequest, final ByteBuffer buffer) 
            throws SerializationException {
        try {
            StringSerializationUtils.serializeString(hedgeRequest.getId(), buffer);
            StringSerializationUtils.serializeString(hedgeRequest.getTickId(), buffer);
            StringSerializationUtils.serializeString(hedgeRequest.getProduct(), buffer);
            StringSerializationUtils.serializeString(hedgeRequest.getInstrument(), buffer);
            StringSerializationUtils.serializeString(hedgeRequest.getType(), buffer);
            hedgeRequest.getLimitTotalPrice().serialize(buffer);
            hedgeRequest.getAmount().serialize(buffer);
            buffer.putChar(hedgeRequest.getSide() == Side.BUY ? 'B' : 'S');
            buffer.putLong(hedgeRequest.getCreationTime());
            
            return buffer.position();
        } catch (Exception e) {
            log.error("Error when serializing HedgeRequest to byte[]", e);
            throw new SerializationException(
                    "Error when serializing HedgeRequest to byte[]", e);
        }
    }


    @Override
    public void close() {
    }
}
