package com.systematicdata.shmax.bus.serializer;

import com.systematicdata.shmax.data.*;
import com.systematicdata.fixmath.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.errors.*;

public class StringSerializationUtils {
    public static String deserializeString(final ByteBuffer buffer) {
        final int length = buffer.getShort();
        final byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    public static void serializeString(final String str, final ByteBuffer buffer) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buffer.putShort((short) bytes.length);  // 2 bytes for lebgth
        buffer.put(bytes); // UTF-8 String
    }
}
