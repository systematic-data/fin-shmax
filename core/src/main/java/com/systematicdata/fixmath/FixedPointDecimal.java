package com.systematicdata.fixmath;

import java.math.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * Non thread safe-Mutable implementation of FixedPointDecimal.
 * Up to 6 decimals precission.
 */
public class FixedPointDecimal {
    public final static boolean POSITIVE = true;
    public final static boolean NEGATIVE = false;

    public static final FixedPointDecimal MAX_VALUE =
            new FixedPointDecimal(true, Integer.MAX_VALUE, 999_999L);

    public static final FixedPointDecimal MIN_VALUE =
            new FixedPointDecimal(false, Integer.MIN_VALUE, 999_999L);


    private static final long DECIMAL_BASE = 1_000_000L; // 6 decimals precission

    // Unsigned positive and negative part
    private long integerPart, decimalPart;

    // Positive sign
    private boolean positive;

    public FixedPointDecimal(final double val) {
        final double value = val>=0 ? val : -val;
        final long integer = (long) value;
        final long decimal = Math.abs(Math.round((value - integer) * DECIMAL_BASE));
        this(val>0, integer, decimal);
    }

    /**
     * Creates a Fixed point object.
     * Do not use.... the "decimalPart" is based on 6 decial precission.

     * @param positive true for greater than zero, false for less than zero.
     * @param decimalPart the decimal part of the number, positive and less than 1e6.
     *      For example 1 means .000001
     * @param integerPart the integer part of the number, might not be negative.
     */
    protected FixedPointDecimal(final boolean positive,
            final long integerPart, final long decimalPart) {
        if(decimalPart<0 || decimalPart >= DECIMAL_BASE) {
            throw new IllegalArgumentException("Decimal part must be positive "
                    + "and less than " + DECIMAL_BASE);
        }
        this.integerPart = integerPart>=0 ? integerPart : -integerPart;
        this.decimalPart = decimalPart;
        this.positive = integerPart>=0 ? positive : !positive;
    }


    public FixedPointDecimal(final String str) {
        final String parts[] = str.split("\\.");
        if(parts[0].length()==0 || parts[0].charAt(0)=='-') {
            this.positive = false;
            this.integerPart = -Long.parseLong(parts[0]);
        } else {
            this.positive = true;
            this.integerPart = Long.parseLong(parts[0]);
        }
        if(parts.length>1) {
            final StringBuilder sb = new StringBuilder(parts[1]);
            while(sb.length()<6) sb.append("0");
            this.decimalPart = Long.parseLong(sb.toString());
        } else {
            this.decimalPart = 0;
        }
    }


    /**
     * Constructor copy.
     */
    public FixedPointDecimal(final FixedPointDecimal x) {
        this(x.positive, x.integerPart, x.decimalPart);
    }


    /**
     * Adds to this number the given one and stores the result in this number.
     */
    public FixedPointDecimal addTo(final FixedPointDecimal x) {
        this.addTo(x.positive, x.integerPart, x.decimalPart);
        return this;
    }

    /**
     * Adds to this number the given one and stores the result in this number.
     */
    public FixedPointDecimal addTo(final long x) {
        this.addTo(x>0, x>0 ? x : -x, 0);
        return this;
    }

    /**
     * Adds to this number the given one and returns a new number with the result.
     */
    public FixedPointDecimal add(final FixedPointDecimal x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.addTo(x);
        return newVal;
    }

    /**
     * Adds to this number the given one and returns a new number with the result.
     */
    public FixedPointDecimal add(final long x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.addTo(x);
        return newVal;
    }

    /**
     * Subtract to this number the given one and stores the result in this number.
     */
    public FixedPointDecimal subtractTo(final long x) {
        this.addTo(-x);
        return this;
    }

    /**
     * Subtract to this number the given one and stores the result in this number.
     */
    public FixedPointDecimal subtractTo(final FixedPointDecimal x) {
        x.positive = !x.positive;
        this.addTo(x);
        x.positive = !x.positive;
        return this;
    }

    /**
     * Subtract to this number the given one and returns a new object with the result.
     */
    public FixedPointDecimal subtract(final FixedPointDecimal x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.subtractTo(x);
        return newVal;
    }

    /**
     * Subtract to this number the given one and returns a new object with the result.
     */
    public FixedPointDecimal subtract(final long x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.subtractTo(x);
        return newVal;
    }


    /**
     * Multiplies this number by given and stores the result in this number.
     */
    public FixedPointDecimal multiplyTo(final FixedPointDecimal x) {
        multiplyTo(x.positive, x.integerPart, x.decimalPart);
        return this;
    }

    /**
     * Multiplies this number by given and stores the result in this number.
     */
    public FixedPointDecimal multiplyTo(final long x) {
        multiplyTo(x>0, x>0 ? x : -x, 0);
        return this;
    }

    /**
     * Multiplies this number by given and returns a new number with the result.
     */
    public FixedPointDecimal multiply(final FixedPointDecimal x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.multiplyTo(x);
        return newVal;
    }

    /**
     * Multiplies this number by given and returns a new number with the result.
     */
    public FixedPointDecimal multiply(final long x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.multiplyTo(x);
        return newVal;
    }

    /**
     * Divides this number by given and stores the result in this number.
     */
    public FixedPointDecimal divideTo(final FixedPointDecimal x) {
        divideTo(x.positive, x.integerPart, x.decimalPart);
        return this;
    }

   /**
     * Divides this number by given and stores the result in this number.
     */
    public FixedPointDecimal divideTo(final long x) {
        divideTo(x>0, x>0 ? x : -x, 0);
        return this;
    }

    /**
     * Divides this number by given and return a new number with the result.
     */
    public FixedPointDecimal divide(final FixedPointDecimal x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.divideTo(x);
        return newVal;
    }


    /**
     * Divides this number by given and return a new number with the result.
     */
    public FixedPointDecimal divide(final long x) {
        final FixedPointDecimal newVal = new FixedPointDecimal(
                this.positive, this.integerPart, this.decimalPart);
        newVal.divideTo(x);
        return newVal;
    }



    public int compareTo(final FixedPointDecimal x) {
        return this.compareTo(x.positive, x.integerPart, x.decimalPart);
    }

    public int compareTo(final long x) {
        return this.compareTo(x>0, x>0 ? x : -x, 0);
    }

    public int compareTo(final double x) {
        long integer = (long) x;
        return this.compareTo(x>0, x>0 ? integer : -integer, 
                Math.abs(Math.round((x - integer) * DECIMAL_BASE)));
    }

    public boolean greaterThan(final FixedPointDecimal x) {
        return this.compareTo(x)>0;
    }

    public boolean smallerThan(final FixedPointDecimal x) {
        return this.compareTo(x)<0;
    }


    /**
     * Returns true only if the value is greater extrictly than zero.
     */
    public boolean isPositive() {
        return this.positive && (this.integerPart>0 || this.decimalPart>0);
    }


    @Override
    public String toString() {
        if(decimalPart>0) {
            String str = Long.toString(this.decimalPart);
            StringBuilder sb = new StringBuilder("0".repeat(6-str.length()));
            long decimal = decimalPart;
            while((decimal/10)*10 == decimal) {
                decimal = decimal/10;
            }
            sb.append(Long.toString(decimal));
            return (this.positive ? "" : "-") + (integerPart + "." + sb);
        } else {
            return (this.positive ? "" : "-") + (integerPart);
        }
    }


    /**
     * Serializes this object into an existent buffer.
     */
    public void serialize(final ByteBuffer buffer) {
        buffer.putChar(this.positive ? '+' : '-');
        buffer.putLong(this.integerPart);
        buffer.putLong(this.decimalPart);
    }


    /**
     * Deserializes from buffer into an object.
     */
    public static FixedPointDecimal deserialize(final ByteBuffer  buffer) {
        final boolean pos = buffer.getChar() == '+';
        final long intPart = buffer.getLong();
        final long decPart = buffer.getLong();
        return new FixedPointDecimal(pos, intPart, decPart);
    }


    // 
    // PRIVATE STUFF 
    //
    private int compareTo(final boolean positive, final long integerPart, 
            final long decimalPart) {
        if(this.positive && !positive) return +1;
        if(!this.positive && positive) return -1;
        if(this.positive && positive) {
            if(this.integerPart != integerPart) {
                return Long.compare(this.integerPart, integerPart);
            } else {
                return Long.compare(this.decimalPart, decimalPart);
            }
        } else { // Both negative
            if(this.integerPart != integerPart) {
                return -Long.compare(this.integerPart, integerPart);
            } else {
                return -Long.compare(this.decimalPart, decimalPart);
            }
        }
    }


    private void multiplyTo(final boolean positive, final long integerPart, 
                final long decimalPart) {
        long a = this.integerPart * DECIMAL_BASE + this.decimalPart;
        long b = integerPart * DECIMAL_BASE + decimalPart;

        long product = a * b; // Full precision

        this.integerPart = (product / DECIMAL_BASE) / DECIMAL_BASE;
        this.decimalPart = (product / DECIMAL_BASE) % DECIMAL_BASE;
        this.positive = (this.positive == positive);
    }


    private void divideTo(final boolean positive, final long integerPart, 
            final long decimalPart) {
        if(integerPart == 0 && decimalPart == 0) {
            throw new ArithmeticException("Division by zero");
        }

        long a = this.integerPart * DECIMAL_BASE + this.decimalPart;
        long b = integerPart * DECIMAL_BASE + decimalPart;

        long quotient = (a * DECIMAL_BASE) / b;
        this.integerPart = quotient / DECIMAL_BASE;
        this.decimalPart = quotient % DECIMAL_BASE;

        this.positive = (this.positive == positive);
    }



    private void addTo(final boolean positive, final long integerPart, 
            final long decimalPart) {
        long newInteger, newDecimal;
        if(this.positive == positive) {
            newInteger = this.integerPart + integerPart;
            newDecimal = this.decimalPart + decimalPart;

            if(newDecimal >= DECIMAL_BASE) {
                newInteger += 1;
                newDecimal -= DECIMAL_BASE;
            }
        } else {
            newInteger = this.integerPart - integerPart;
            newDecimal = this.decimalPart - decimalPart;
            if(this.integerPart<integerPart || 
                    (this.integerPart==integerPart && this.decimalPart<decimalPart)) {
                this.positive = !this.positive;
                newInteger = -newInteger;
                newDecimal = -newDecimal;
            }
            if (newDecimal < 0) {
                newInteger -= 1;
                newDecimal += DECIMAL_BASE;
            }
        }
        this.integerPart = newInteger;
        this.decimalPart = newDecimal;
    }
}
