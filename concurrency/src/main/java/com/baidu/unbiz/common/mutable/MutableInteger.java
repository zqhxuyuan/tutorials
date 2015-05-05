package com.baidu.unbiz.common.mutable;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午4:28:21
 */
public final class MutableInteger extends Number implements Comparable<MutableInteger>, Cloneable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2950981847144907872L;

    public MutableInteger() {
    }

    public MutableInteger(int value) {
        this.value = value;
    }

    public MutableInteger(String value) {
        this.value = Integer.parseInt(value);
    }

    public MutableInteger(Number number) {
        this.value = number.intValue();
    }

    // ---------------------------------------------------------------- value

    /**
     * The mutable value.
     */
    public int value;

    /**
     * Returns mutable value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets mutable value.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Sets mutable value from a Number.
     */
    public void setValue(Number value) {
        this.value = value.intValue();
    }

    // ---------------------------------------------------------------- object

    /**
     * Stringify the value.
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }

    /**
     * Returns a hashcode for this value.
     */
    @Override
    public int hashCode() {
        return value;
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
            if (ClassUtil.isInstance(Integer.class, obj)) {
                return obj.equals(value);
            }
            if (ClassUtil.isInstance(MutableInteger.class, obj)) {
                return value == ((MutableInteger) obj).value;
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
        return value;
    }

    /**
     * Returns the value as a long.
     */
    @Override
    public long longValue() {
        return value;
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
     * Compares value of two same instances.
     */
    public int compareTo(MutableInteger other) {
        return value < other.value ? -1 : (value == other.value ? 0 : 1);
    }

    // ---------------------------------------------------------------- clone

    /**
     * Clones object.
     */
    @Override
    public MutableInteger clone() {
        return new MutableInteger(value);
    }
}