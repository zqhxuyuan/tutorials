package com.baidu.unbiz.common.mutable;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午4:28:15
 */
public final class MutableFloat extends Number implements Comparable<MutableFloat>, Cloneable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1115554883981652299L;

    public MutableFloat() {
    }

    public MutableFloat(float value) {
        this.value = value;
    }

    public MutableFloat(String value) {
        this.value = Float.parseFloat(value);
    }

    public MutableFloat(Number number) {
        this.value = number.floatValue();
    }

    // ---------------------------------------------------------------- value

    /**
     * The mutable value.
     */
    public float value;

    /**
     * Returns mutable value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets mutable value.
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Sets mutable value from a Number.
     */
    public void setValue(Number value) {
        this.value = value.floatValue();
    }

    // ---------------------------------------------------------------- object

    /**
     * Stringify the value.
     */
    @Override
    public String toString() {
        return Float.toString(value);
    }

    /**
     * Returns a hashcode for this value.
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
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
            if (ClassUtil.isInstance(Float.class, obj)) {
                return obj.equals(value);
            }
            if (ClassUtil.isInstance(MutableFloat.class, obj)) {
                return Float.floatToIntBits(value) == Float.floatToIntBits(((MutableFloat) obj).value);
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
     * Returns the value as a float.
     */
    @Override
    public float floatValue() {
        return value;
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
        return Float.isNaN(value);
    }

    /**
     * Checks whether the float value is infinite.
     */
    public boolean isInfinite() {
        return Float.isInfinite(value);
    }

    /**
     * Compares value of two same instances.
     */
    public int compareTo(MutableFloat other) {
        return Float.compare(value, other.value);
    }

    // ---------------------------------------------------------------- clone

    /**
     * Clones object.
     */
    @Override
    public MutableFloat clone() {
        return new MutableFloat(value);
    }

}