package com.baidu.unbiz.common;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月22日 下午5:11:08
 */
public abstract class HashCode {

    public static final int SEED = 173;

    public static final int PRIME = 37;

    public static int hash(int seed, boolean aBoolean) {
        return (PRIME * seed) + (aBoolean ? 1231 : 1237);
    }

    public static int hash(int seed, boolean[] booleanArray) {
        if (booleanArray == null) {
            return 0;
        }
        for (boolean aBoolean : booleanArray) {
            seed = hash(seed, aBoolean);
        }
        return seed;
    }

    public static int hashBooleanArray(int seed, boolean...booleanArray) {
        return hash(seed, booleanArray);
    }

    public static int hash(int seed, char aChar) {
        return (PRIME * seed) + (int) aChar;
    }

    public static int hash(int seed, char[] charArray) {
        if (charArray == null) {
            return 0;
        }
        for (char aChar : charArray) {
            seed = hash(seed, aChar);
        }
        return seed;
    }

    public static int hashCharArray(int seed, char...charArray) {
        return hash(seed, charArray);
    }

    public static int hash(int seed, int anInt) {
        return (PRIME * seed) + anInt;
    }

    public static int hash(int seed, int[] intArray) {
        if (intArray == null) {
            return 0;
        }
        for (int anInt : intArray) {
            seed = hash(seed, anInt);
        }
        return seed;
    }

    public static int hashIntArray(int seed, int...intArray) {
        return hash(seed, intArray);
    }

    public static int hash(int seed, short[] shortArray) {
        if (shortArray == null) {
            return 0;
        }
        for (short aShort : shortArray) {
            seed = hash(seed, aShort);
        }
        return seed;
    }

    public static int hashShortArray(int seed, short...shortArray) {
        return hash(seed, shortArray);
    }

    public static int hash(int seed, byte[] byteArray) {
        if (byteArray == null) {
            return 0;
        }
        for (byte aByte : byteArray) {
            seed = hash(seed, aByte);
        }
        return seed;
    }

    public static int hashByteArray(int seed, byte...byteArray) {
        return hash(seed, byteArray);
    }

    public static int hash(int seed, long aLong) {
        return (PRIME * seed) + (int) (aLong ^ (aLong >>> 32));
    }

    public static int hash(int seed, long[] longArray) {
        if (longArray == null) {
            return 0;
        }
        for (long aLong : longArray) {
            seed = hash(seed, aLong);
        }
        return seed;
    }

    public static int hashLongArray(int seed, long...longArray) {
        return hash(seed, longArray);
    }

    public static int hash(int seed, float aFloat) {
        return hash(seed, Float.floatToIntBits(aFloat));
    }

    public static int hash(int seed, float[] floatArray) {
        if (floatArray == null) {
            return 0;
        }
        for (float aFloat : floatArray) {
            seed = hash(seed, aFloat);
        }
        return seed;
    }

    public static int hashFloatArray(int seed, float...floatArray) {
        return hash(seed, floatArray);
    }

    public static int hash(int seed, double aDouble) {
        return hash(seed, Double.doubleToLongBits(aDouble));
    }

    public static int hash(int seed, double[] doubleArray) {
        if (doubleArray == null) {
            return 0;
        }
        for (double aDouble : doubleArray) {
            seed = hash(seed, aDouble);
        }
        return seed;
    }

    public static int hashDoubleArray(int seed, double...doubleArray) {
        return hash(seed, doubleArray);
    }

    public static int hash(int seed, Object aObject) {
        int result = seed;
        if (aObject == null) {
            return hash(result, 0);
        }
        if (!aObject.getClass().isArray()) {
            return hash(result, aObject.hashCode());
        }

        Object[] objects = (Object[]) aObject;
        int length = objects.length;
        for (int idx = 0; idx < length; ++idx) {
            result = hash(result, objects[idx]);
        }

        return result;
    }

}