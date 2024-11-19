package com.systematicdata.fixmath;

import java.math.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * At the moment, implemented using BigDecimal.
 */
public class FixedPointDecimal {
    public static final FixedPointDecimal MAX_VALUE =
            new FixedPointDecimal(Integer.MAX_VALUE, 999999999);

    public static final FixedPointDecimal MIN_VALUE =
            new FixedPointDecimal(Integer.MIN_VALUE, 0);



    private BigDecimal val;

    protected FixedPointDecimal(final BigDecimal val) {
        this.val = val;
    }

    public FixedPointDecimal(final int integerPart, final int decimalPart) {
        this.val = new BigDecimal(integerPart + "." + decimalPart);
    }

    public void add(final FixedPointDecimal x) {
        this.val = this.val.add(x.val);
    }

    public void add(final long x) {
        this.val = this.val.add(new BigDecimal(x));
    }


    public void subtract(final FixedPointDecimal x) {
        this.val = this.val.subtract(x.val);
    }
    public void subtract(final long x) {
        this.val = this.val.subtract(new BigDecimal(x));
    }

    public void multiply(final FixedPointDecimal x) {
        this.val = this.val.multiply(x.val);
    }

    public void multiply(final long x) {
        this.val = this.val.multiply(new BigDecimal(x));
    }

    public void divide(final FixedPointDecimal x) {
        this.val = this.val.divide(x.val);
    }

    public void divide(final long x) {
        this.val = this.val.divide(new BigDecimal(x));
    }


    public int compareTo(final FixedPointDecimal x) {
        return this.val.compareTo(x.val);
    }

    public int compareTo(final long x) {
        return this.val.compareTo(new BigDecimal(x));
    }

    public int compareTo(final double x) {
        return this.val.compareTo(new BigDecimal(x));
    }

    public boolean greaterThan(final FixedPointDecimal x) {
        return this.compareTo(x)>0;
    }

    public boolean smallerThan(final FixedPointDecimal x) {
        return this.compareTo(x)<0;
    }


    @Override
    public String toString() {
        return this.val.toString();
    }

    /**
     * Serializes this object into an existent buffer.
     */
    public void serialize(final ByteBuffer buffer) {
        final String str = this.val.toString();
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buffer.putShort((short) str.length());
        buffer.put(bytes);
    }


    /**
     * Deserializes from buffer into an object.
     */
    public static FixedPointDecimal deserialize(final ByteBuffer  buffer) {
        final short length = buffer.getShort();
        final byte[] bytes = new byte[length];
        buffer.get(bytes);
        final BigDecimal val = new BigDecimal(new String(bytes,
                StandardCharsets.UTF_8));
        return new FixedPointDecimal(val);
    }
}
