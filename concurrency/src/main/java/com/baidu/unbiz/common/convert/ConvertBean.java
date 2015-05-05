package com.baidu.unbiz.common.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午2:18:05
 */
public class ConvertBean {

    protected ObjectConverter<?> objectConverter = ObjectConverter.getInstance();

    public Boolean toBoolean(Object value) {
        return (Boolean) objectConverter.toConvert(value, Boolean.class);
    }

    public Boolean toBoolean(Object value, Boolean defaultValue) {
        Boolean result = (Boolean) objectConverter.toConvert(value, Boolean.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public boolean toBooleanValue(Object value, boolean defaultValue) {
        Boolean result = (Boolean) objectConverter.toConvert(value, boolean.class);
        if (result == null) {
            return defaultValue;
        }
        return result.booleanValue();
    }

    public boolean toBooleanValue(Object value) {
        return toBooleanValue(value, false);
    }

    public Integer toInteger(Object value) {
        return (Integer) objectConverter.toConvert(value, Integer.class);
    }

    public Integer toInteger(Object value, Integer defaultValue) {
        Integer result = (Integer) objectConverter.toConvert(value, Integer.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public int toIntValue(Object value, int defaultValue) {
        Integer result = (Integer) objectConverter.toConvert(value, int.class);
        if (result == null) {
            return defaultValue;
        }
        return result.intValue();
    }

    public int toIntValue(Object value) {
        return toIntValue(value, 0);
    }

    public Long toLong(Object value) {
        return (Long) objectConverter.toConvert(value, Long.class);
    }

    public Long toLong(Object value, Long defaultValue) {
        Long result = (Long) objectConverter.toConvert(value, Long.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public long toLongValue(Object value, long defaultValue) {
        Long result = (Long) objectConverter.toConvert(value, long.class);
        if (result == null) {
            return defaultValue;
        }
        return result.longValue();
    }

    public long toLongValue(Object value) {
        return toLongValue(value, 0);
    }

    public Float toFloat(Object value) {
        return (Float) objectConverter.toConvert(value, Float.class);
    }

    public Float toFloat(Object value, Float defaultValue) {
        Float result = (Float) objectConverter.toConvert(value, Float.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public float toFloatValue(Object value, float defaultValue) {
        Float result = (Float) objectConverter.toConvert(value, float.class);
        if (result == null) {
            return defaultValue;
        }
        return result.floatValue();
    }

    public float toFloatValue(Object value) {
        return toFloatValue(value, 0);
    }

    public Double toDouble(Object value) {
        return (Double) objectConverter.toConvert(value, Double.class);
    }

    public Double toDouble(Object value, Double defaultValue) {
        Double result = (Double) objectConverter.toConvert(value, Double.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public double toDoubleValue(Object value, double defaultValue) {
        Double result = (Double) objectConverter.toConvert(value, double.class);
        if (result == null) {
            return defaultValue;
        }
        return result.doubleValue();
    }

    public double toDoubleValue(Object value) {
        return toDoubleValue(value, 0);
    }

    public Short toShort(Object value) {
        return (Short) objectConverter.toConvert(value, Short.class);
    }

    public Short toShort(Object value, Short defaultValue) {
        Short result = (Short) objectConverter.toConvert(value, Short.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public short toShortValue(Object value, short defaultValue) {
        Short result = (Short) objectConverter.toConvert(value, short.class);
        if (result == null) {
            return defaultValue;
        }
        return result.shortValue();
    }

    public short toShortValue(Object value) {
        return toShortValue(value, (short) 0);
    }

    public Character toCharacter(Object value) {
        return (Character) objectConverter.toConvert(value, Character.class);
    }

    public Character toCharacter(Object value, Character defaultValue) {
        Character result = (Character) objectConverter.toConvert(value, Character.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public char toCharValue(Object value, char defaultValue) {
        Character result = (Character) objectConverter.toConvert(value, char.class);
        if (result == null) {
            return defaultValue;
        }
        return result.charValue();
    }

    public char toCharValue(Object value) {
        return toCharValue(value, (char) 0);
    }

    public Byte toByte(Object value) {
        return (Byte) objectConverter.toConvert(value, Byte.class);
    }

    public Byte toByte(Object value, Byte defaultValue) {
        Byte result = (Byte) objectConverter.toConvert(value, Byte.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public byte toByteValue(Object value, byte defaultValue) {
        Byte result = (Byte) objectConverter.toConvert(value, byte.class);
        if (result == null) {
            return defaultValue;
        }
        return result.byteValue();
    }

    public byte toByteValue(Object value) {
        return toByteValue(value, (byte) 0);
    }

    public boolean[] toBooleanArray(Object value) {
        return (boolean[]) objectConverter.toConvert(value, boolean[].class);
    }

    public int[] toIntegerArray(Object value) {
        return (int[]) objectConverter.toConvert(value, int[].class);
    }

    public long[] toLongArray(Object value) {
        return (long[]) objectConverter.toConvert(value, long[].class);
    }

    public float[] toFloatArray(Object value) {
        return (float[]) objectConverter.toConvert(value, float[].class);
    }

    public double[] toDoubleArray(Object value) {
        return (double[]) objectConverter.toConvert(value, double[].class);
    }

    public short[] toShortArray(Object value) {
        return (short[]) objectConverter.toConvert(value, short[].class);
    }

    public char[] toCharacterArray(Object value) {
        return (char[]) objectConverter.toConvert(value, char[].class);
    }

    public String toString(Object value) {
        return (String) objectConverter.toConvert(value, String.class);
    }

    public String toString(Object value, String defaultValue) {
        String result = (String) objectConverter.toConvert(value, String.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public String[] toStringArray(Object value) {
        return (String[]) objectConverter.toConvert(value, String[].class);
    }

    public Class<?> toClass(Object value) {
        return (Class<?>) objectConverter.toConvert(value, Class.class);
    }

    public Class<?>[] toClassArray(Object value) {
        return (Class<?>[]) objectConverter.toConvert(value, Class[].class);
    }

    public Date toDate(Object value) {
        return (Date) objectConverter.toConvert(value, Date.class);
    }

    public Date toDate(Object value, Date defaultValue) {
        Date result = (Date) objectConverter.toConvert(value, Date.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public Calendar toCalendar(Object value) {
        return (Calendar) objectConverter.toConvert(value, Calendar.class);
    }

    public Calendar toCalendar(Object value, Calendar defaultValue) {
        Calendar result = (Calendar) objectConverter.toConvert(value, Calendar.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public BigInteger toBigInteger(Object value) {
        return (BigInteger) objectConverter.toConvert(value, BigInteger.class);
    }

    public BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        BigInteger result = (BigInteger) objectConverter.toConvert(value, BigInteger.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    public BigDecimal toBigDecimal(Object value) {
        return (BigDecimal) objectConverter.toConvert(value, BigDecimal.class);
    }

    public BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        BigDecimal result = (BigDecimal) objectConverter.toConvert(value, BigDecimal.class);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
}