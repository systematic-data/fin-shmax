package com.systematicdata.fixmath;

import java.nio.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FixedPointDecimalTest {
    @Test
    public void testToString() {
        assertEquals("1.02345", new FixedPointDecimal("1.02345").toString());
        assertEquals("1.2345", new FixedPointDecimal("1.2345").toString());
        assertEquals("-1.2345", new FixedPointDecimal("-1.2345").toString());
        assertEquals("1.2345", new FixedPointDecimal(1.2345).toString());
        assertEquals("-1.2345", new FixedPointDecimal(-1.2345).toString());
    }

    @Test
    public void testAdd() {
        FixedPointDecimal a;
        FixedPointDecimal b;

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("2.34567");
        a.add(b);
        assertEquals("3.58017", a.toString());

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("2.34567");
        a.add(b);
        assertEquals("3.58017", a.toString());

        a = new FixedPointDecimal("1.2345");
        a.add(1);
        assertEquals("2.2345", a.toString());

        a = new FixedPointDecimal("1.2345");
        a.add(-1);
        assertEquals("0.2345", a.toString());

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("-2.34567");
        assertEquals("-1.11117", a.add(b).toString());
    }

    @Test
    public void testSubtract() {
        FixedPointDecimal a;
        FixedPointDecimal b;

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("2.34567");
        a.subtract(b);
        assertEquals("-1.11117", a.toString());

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("2.34567");
        a.subtract(1);
        assertEquals("0.2345", a.toString());

        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("2.34567");
        a.subtract(-1);
        assertEquals("2.2345", a.toString());
 
        a = new FixedPointDecimal("1.2345");
        b = new FixedPointDecimal("-2.34567");
        a.subtract(b);
        assertEquals("3.58017", a.toString());
    }

    @Test
    public void testMultiply() {
    }

    @Test
    public void testDivide() {
    }

    @Test
    public void testSerialize() throws Exception {
        FixedPointDecimal a = new FixedPointDecimal("1.02345");
        final ByteBuffer buffer = ByteBuffer.allocate(4096);
        a.serialize(buffer);
        buffer.rewind();
        FixedPointDecimal b = FixedPointDecimal.deserialize(buffer);
        assertEquals(a.compareTo(b),0);
    }
}
