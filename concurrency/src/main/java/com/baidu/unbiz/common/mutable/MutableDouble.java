package com.baidu.unbiz.common.mutable;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午4:28:08
 */
public final class MutableDouble extends Number implements Comparable<MutableDouble>, Cloneable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4925002798148567494L;

    public MutableDouble() {
    }

    public MutableDouble(double value) {
        this.value = value;
    }

    public MutableDouble(String value) {
        this.value = Double.parseDouble(value);
    }

    public MutableDouble(Number number) {
        this.value = number.doubleValue();
    }

    // ---------------------------------------------------------------- value

    /**
     * The mutable value.
     */
    public double value;

    /**
     * Returns mutable value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets mutable value.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Sets mutable value from a Number.
     */
    public void setValue(Number value) {
        this.value = value.doubleValue();
    }

    // ---------------------------------------------------------------- object

    /**
     * Stringify the value.
     */
    @Override
    public String toString() {
        return Double.toString(value);
    }

    /**
     * Returns a hashcode for this value.
     */
    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    /**
     * Compares this object to the specified object.
     * 
     * @param obj the object to compare with.
     * @return <code>true</code> if the objects are the same; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (ClassUtil.isInstance(Double.class, obj)) {
                return obj.equals(value);
            }
            if (ClassUtil.isInstance(MutableDouble.class, obj)) {
                return Double.doubleToLongBits(value) == Double.doubleToLongBits(((MutableDouble) obj).value);
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- number

    /**
     * Returns the value as a int.
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value as a long.
     */
    @Override
    public long longValue() {
        return (long) value;
    }

    /**
     * Returns the value as a float..
     */
    @Override
    public float floatValue() {
        return (float) value;
    }

    /**
     * Returns the value as a double.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    // ---------------------------------------------------------------- compare

    /**
     * Checks whether the value is the special NaN value.
     */
    public boolean isNaN() {
        return Double.isNaN(value);
    }

    /**
     * Checks whether the double value is infinite.
     */
    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    /**
     * Compares value of two same instances.
     */
    public int compareTo(MutableDouble other) {
        return Double.compare(value, other.value);
    }

    // ---------------------------------------------------------------- clone

    /**
     * Clones object.
     */
    @Override
    public MutableDouble clone() {
        return new MutableDouble(value);
    }

}