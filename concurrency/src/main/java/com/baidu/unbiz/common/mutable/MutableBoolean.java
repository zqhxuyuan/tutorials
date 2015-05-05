package com.baidu.unbiz.common.mutable;

import java.io.Serializable;

import com.baidu.unbiz.common.ClassUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午4:27:51
 */
public final class MutableBoolean implements Comparable<MutableBoolean>, Cloneable, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -601670961902893139L;

    public MutableBoolean() {
    }

    public MutableBoolean(boolean value) {
        this.value = value;
    }

    public MutableBoolean(String value) {
        this.value = Boolean.valueOf(value).booleanValue();
    }

    public MutableBoolean(Boolean value) {
        this.value = value.booleanValue();
    }

    public MutableBoolean(Number number) {
        this.value = number.intValue() != 0;
    }

    // ---------------------------------------------------------------- value

    /**
     * The mutable value.
     */
    public boolean value;

    /**
     * Returns mutable value.
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Sets mutable value.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    public void setValue(Boolean value) {
        this.value = value.booleanValue();
    }

    // ---------------------------------------------------------------- object

    /**
     * Stringify the value.
     */
    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    /**
     * Returns a hashcode for this value.
     */
    @Override
    public int hashCode() {
        return value ? 1231 : 1237;
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
            if (ClassUtil.isInstance(Boolean.class, obj)) {
                return obj.equals(value);
            }
            if (ClassUtil.isInstance(MutableBoolean.class, obj)) {
                return value == ((MutableBoolean) obj).value;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- compare

    /**
     * Compares value of two same instances.
     */
    public int compareTo(MutableBoolean o) {
        return (value == o.value) ? 0 : (!value ? -1 : 1);
    }

    // ---------------------------------------------------------------- clone

    /**
     * Clones object.
     */
    @Override
    public MutableBoolean clone() {
        return new MutableBoolean(value);
    }
}
