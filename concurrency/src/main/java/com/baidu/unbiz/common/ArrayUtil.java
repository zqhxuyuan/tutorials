/**
 * 
 */
package com.baidu.unbiz.common;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;

import com.baidu.unbiz.common.lang.PrimitiveArray;

/**
 * 有关数组处理的工具类。FIXME
 * <p>
 * 这个类中的每个方法都可以“安全”地处理<code>null</code>，而不会抛出<code>NullPointerException</code>。
 * </p>
 * <p>
 * 本工具类是对JDK <code>Arrays</code>的补充。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月7日 下午7:57:38
 */
public abstract class ArrayUtil {

    private static final int INDEX_NOT_FOUND = -1;

    // ==========================================================================
    // 取得数组长度。
    // ==========================================================================

    private static int length(Object array, int defaultIfNull, int defaultIfNotArray) {
        if (array == null) {
            return defaultIfNull; // null
        }
        if (array instanceof Object[]) {
            return ((Object[]) array).length;
        }
        if (array instanceof long[]) {
            return ((long[]) array).length;
        }
        if (array instanceof int[]) {
            return ((int[]) array).length;
        }
        if (array instanceof short[]) {
            return ((short[]) array).length;
        }
        if (array instanceof byte[]) {
            return ((byte[]) array).length;
        }
        if (array instanceof double[]) {
            return ((double[]) array).length;
        }
        if (array instanceof float[]) {
            return ((float[]) array).length;
        }
        if (array instanceof boolean[]) {
            return ((boolean[]) array).length;
        }
        if (array instanceof char[]) {
            return ((char[]) array).length;
        }
        return defaultIfNotArray; // not an array
    }

    // ==========================================================================
    // 判空函数。
    //
    // 判断一个数组是否为null或包含0个元素。
    // ==========================================================================

    // ==========================================================================
    // 取得数组长度。
    // ==========================================================================

    /**
     * 取得数组的长度。FIXME，先这样
     * <p>
     * 此方法比<code>Array.getLength()</code>要快得多。
     * </p>
     * 
     * @param array 要检查的数组
     * @return 如果为空，或者非数组，则返回<code>0</code>。
     */
    public static int length(Object array) {
        return length(array, 0, 0);
    }

    /**
     * 检查数组是否为<code>null</code>或空数组<code>[]</code>。
     * <p/>
     * 
     * <pre>
     * ArrayUtil.isEmpty(null)              = true
     * ArrayUtil.isEmpty(new int[0])        = true
     * ArrayUtil.isEmpty(new int[10])       = false
     * </pre>
     * 
     * @param array 要检查的数组
     * @return 如果为空, 则返回<code>true</code>
     */
    public static boolean isEmpty(Object array) {
        return length(array, 0, INDEX_NOT_FOUND) == 0;
    }

    /**
     * 检查数组是否为<code>null</code>或空数组<code>[]</code>。
     * <p/>
     * 
     * <pre>
     * ArrayUtil.isNotEmpty(null)              = false
     * ArrayUtil.isNotEmpty(new int[0])        = false
     * ArrayUtil.isNotEmpty(new int[10])       = true
     * </pre>
     * 
     * @param array 要检查的数组
     * @return 如果不为空, 则返回<code>true</code>
     */
    public static boolean isNotEmpty(Object array) {
        return length(array, 0, INDEX_NOT_FOUND) > 0;
    }

    // ==========================================================================
    // 默认值函数。
    //
    // 当数组为空时，取得默认数组值。
    // 注：判断数组为null时，可用更通用的ObjectUtil.defaultIfNull。
    // ==========================================================================

    /**
     * 如果数组是<code>null</code>或空数组<code>[]</code>，则返回指定数组默认值。
     * <p/>
     * 
     * <pre>
     * ArrayUtil.defaultIfEmpty(null, defaultArray)           = defaultArray
     * ArrayUtil.defaultIfEmpty(new String[0], defaultArray)  = 数组本身
     * ArrayUtil.defaultIfEmpty(new String[10], defaultArray) = 数组本身
     * </pre>
     * 
     * @param array 要转换的数组
     * @param defaultArray 默认数组
     * @return 数组本身或默认数组
     */
    public static <T, S extends T> T defaultIfEmpty(T array, S defaultValue) {
        return isEmpty(array) ? defaultValue : array;
    }

    // ==========================================================================
    // 将数组转换成集合类。
    // ==========================================================================

    /**
     * 将数组转换成<code>Iterable</code>列表。
     * <p>
     * 如果输入数组为<code>null</code>，则视作空数组。
     * </p>
     * <p>
     * 该方法返回的<code>Iterable</code>对象是轻量而高效的，不会产生复制数组的开销。你可以使用
     * <code>CollectionUtil.createArrayList(asIterable(componentType, array))</code>或
     * <code>CollectionUtil.createLinkedList(asIterable(componentType, array))</code> 来进一步将 <code>Iterable</code>
     * 转换成指定类型的 <code>List</code>对象。
     * </p>
     * 
     * @param componentType <code>Iterable</code>元素的类型，必须和数组类型兼容。例如对于 <code>int[]</code> 数组， <code>componentType</code>
     *            必须为 <code>Integer.class</code>。
     * @param array 要转换的数组
     * @return 被创建的<code>Iterable</code>对象
     */
    public static <T> Iterable<T> arrayAsIterable(final Class<T> componentType, Object array) {
        Assert.assertNotNull(componentType, "componentType");

        if (array == null) {
            return new ArrayIterable<T>(0) {
                @Override
                protected T get(int i) {
                    Assert.unreachableCode();
                    return null;
                }
            };
        }
        if (array instanceof Object[]) {
            final Object[] objectArray = (Object[]) array;

            return new ArrayIterable<T>(objectArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(objectArray[i]);
                }
            };
        } else if (array instanceof int[]) {
            final int[] intArray = (int[]) array;

            return new ArrayIterable<T>(intArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(intArray[i]);
                }
            };
        } else if (array instanceof long[]) {
            final long[] longArray = (long[]) array;

            return new ArrayIterable<T>(longArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(longArray[i]);
                }
            };
        } else if (array instanceof short[]) {
            final short[] shortArray = (short[]) array;

            return new ArrayIterable<T>(shortArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(shortArray[i]);
                }
            };
        } else if (array instanceof byte[]) {
            final byte[] byteArray = (byte[]) array;

            return new ArrayIterable<T>(byteArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(byteArray[i]);
                }
            };
        } else if (array instanceof double[]) {
            final double[] doubleArray = (double[]) array;

            return new ArrayIterable<T>(doubleArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(doubleArray[i]);
                }
            };
        } else if (array instanceof float[]) {
            final float[] floatArray = (float[]) array;

            return new ArrayIterable<T>(floatArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(floatArray[i]);
                }
            };
        } else if (array instanceof boolean[]) {
            final boolean[] booleanArray = (boolean[]) array;

            return new ArrayIterable<T>(booleanArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(booleanArray[i]);
                }
            };
        } else if (array instanceof char[]) {
            final char[] charArray = (char[]) array;

            return new ArrayIterable<T>(charArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(charArray[i]);
                }
            };
        } else {
            throw new IllegalArgumentException(array + " is not an array");
        }
    }

    private static abstract class ArrayIterable<T> implements Iterable<T> {
        private final int length;

        public ArrayIterable(int length) {
            this.length = length;
        }

        public final Iterator<T> iterator() {
            return new Iterator<T>() {
                private int index;

                public final boolean hasNext() {
                    return index < length;
                }

                public final T next() {
                    if (index >= length) {
                        throw new ArrayIndexOutOfBoundsException(index);
                    }

                    return get(index++);
                }

                public final void remove() {
                    Assert.unsupportedOperation("remove");
                }
            };
        }

        protected abstract T get(int i);
    }

    /**
     * 将数组转换成<code>Map</code>。数组的元素必须是元素个数多于2的子数组。
     * <p/>
     * 
     * <pre>
     * Map colorMap = ArrayUtil.toMap(new String[][] {{
     *     {"RED", 0xFF0000},
     *     {"GREEN", 0x00FF00},
     *     {"BLUE", 0x0000FF}}, String.class, Integer.class);
     * </pre>
     * 
     * @param keyValueArray 要转换的数组
     * @param keyType key的类型，数组元素<code>keyValueArray[n][0]</code>的类型必须与之兼容
     * @param valueType value的类型，数组元素<code>keyValueArray[n][1]</code>的类型必须与之兼容
     * @return 被创建的map
     */
    public static <K, V> Map<K, V> toMap(Object[][] keyValueArray, Class<K> keyType, Class<V> valueType) {
        return toMap(keyValueArray, keyType, valueType, null);
    }

    /**
     * 将数组转换成<code>Map</code>。数组的元素必须是元素个数多于2的子数组。
     * <p/>
     * 
     * <pre>
     * Map colorMap = ArrayUtil.toMap(new String[][] {{
     *     {"RED", 0xFF0000},
     *     {"GREEN", 0x00FF00},
     *     {"BLUE", 0x0000FF}}, String.class, Integer.class, map);
     * </pre>
     * 
     * @param keyValueArray 要转换的数组
     * @param keyType key的类型，数组元素<code>keyValueArray[n][0]</code>的类型必须与之兼容
     * @param valueType value的类型，数组元素<code>keyValueArray[n][1]</code>的类型必须与之兼容
     * @param map 要填充的map，如果为<code>null</code>则自动创建之
     * @return 被创建或填充的map
     */
    public static <K, V> Map<K, V> toMap(Object[][] keyValueArray, Class<K> keyType, Class<V> valueType, Map<K, V> map) {
        Assert.assertNotNull(keyType, "keyType");
        Assert.assertNotNull(valueType, "valueType");

        if (keyValueArray == null) {
            return map;
        }

        if (map == null) {
            map = CollectionUtil.createLinkedHashMap((int) (keyValueArray.length * 1.5));
        }

        for (int i = 0; i < keyValueArray.length; i++) {
            Object[] keyValue = keyValueArray[i];
            Object[] entry = keyValue;

            if (entry == null || entry.length < 2) {
                throw new IllegalArgumentException("Array element " + i + " is not an array of 2 elements");
            }

            map.put(keyType.cast(entry[0]), valueType.cast(entry[1]));
        }

        return map;
    }

    // ==========================================================================
    // 比较数组的长度。
    // ==========================================================================

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(Object[] array1, Object[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(long[] array1, long[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(int[] array1, int[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(short[] array1, short[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(byte[] array1, byte[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(double[] array1, double[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(float[] array1, float[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(boolean[] array1, boolean[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * 判断两个数组是否具有相同的长度。如果数组为<code>null</code>则被看作长度为<code>0</code>。
     * 
     * @param array1 数组1
     * @param array2 数组2
     * @return 如果两个数组长度相同，则返回<code>true</code>
     */
    public static boolean isSameLength(char[] array1, char[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    // ==========================================================================
    // 反转数组的元素顺序。
    // ==========================================================================

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(Object[] array) {
        if (array == null) {
            return;
        }

        Object tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(long[] array) {
        if (array == null) {
            return;
        }

        long tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(int[] array) {
        if (array == null) {
            return;
        }

        int tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(short[] array) {
        if (array == null) {
            return;
        }

        short tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }

        byte tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(double[] array) {
        if (array == null) {
            return;
        }

        double tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(float[] array) {
        if (array == null) {
            return;
        }

        float tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(boolean[] array) {
        if (array == null) {
            return;
        }

        boolean tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * 反转数组的元素顺序。如果数组为<code>null</code>，则什么也不做。
     * 
     * @param array 要反转的数组
     */
    public static void reverse(char[] array) {
        if (array == null) {
            return;
        }

        char tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：Object[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param objectToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(Object[] array, Object[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param objectToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        Object first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && !ObjectUtil.isEquals(array[i], first)) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (!ObjectUtil.isEquals(array[j++], arrayToFind[k++])) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param objectToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(Object[] array, Object[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param objectToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i >= 0; i--) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        Object last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && !ObjectUtil.isEquals(array[i], last)) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (!ObjectUtil.isEquals(array[j--], arrayToFind[k--])) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param objectToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(Object[] array, Object[] arrayToFind) {
        return lastIndexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：long[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param longToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(long[] array, long longToFind) {
        return indexOf(array, longToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(long[] array, long[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param longToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (longToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        long first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param longToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(long[] array, long longToFind) {
        return lastIndexOf(array, longToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(long[] array, long[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param longToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (longToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        long last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param longToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(long[] array, long longToFind) {
        return indexOf(array, longToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(long[] array, long[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：int[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param intToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(int[] array, int intToFind) {
        return indexOf(array, intToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(int[] array, int[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param intToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (intToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param intToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(int[] array, int intToFind) {
        return lastIndexOf(array, intToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(int[] array, int[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param intToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (intToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        int last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param intToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(int[] array, int intToFind) {
        return indexOf(array, intToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(int[] array, int[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：short[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param shortToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(short[] array, short shortToFind) {
        return indexOf(array, shortToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(short[] array, short[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param shortToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (shortToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        short first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param shortToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(short[] array, short shortToFind) {
        return lastIndexOf(array, shortToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(short[] array, short[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param shortToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (shortToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        short last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param shortToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(short[] array, short shortToFind) {
        return indexOf(array, shortToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(short[] array, short[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：byte[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param byteToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(byte[] array, byte byteToFind) {
        return indexOf(array, byteToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(byte[] array, byte[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param byteToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (byteToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        byte first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param byteToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(byte[] array, byte byteToFind) {
        return lastIndexOf(array, byteToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(byte[] array, byte[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param byteToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (byteToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        byte last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param byteToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(byte[] array, byte byteToFind) {
        return indexOf(array, byteToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(byte[] array, byte[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：double[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double doubleToFind) {
        return indexOf(array, doubleToFind, 0, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double doubleToFind, double tolerance) {
        return indexOf(array, doubleToFind, 0, tolerance);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double[] arrayToFind) {
        return indexOf(array, arrayToFind, 0, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double[] arrayToFind, double tolerance) {
        return indexOf(array, arrayToFind, 0, tolerance);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double doubleToFind, int startIndex) {
        return indexOf(array, doubleToFind, startIndex, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double doubleToFind, int startIndex, double tolerance) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        double min = doubleToFind - tolerance;
        double max = doubleToFind + tolerance;

        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double[] arrayToFind, int startIndex) {
        return indexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(double[] array, double[] arrayToFind, int startIndex, double tolerance) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        double firstMin = arrayToFind[0] - tolerance;
        double firstMax = arrayToFind[0] + tolerance;
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && (array[i] < firstMin || array[i] > firstMax)) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (Math.abs(array[j++] - arrayToFind[k++]) > tolerance) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double doubleToFind) {
        return lastIndexOf(array, doubleToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double doubleToFind, double tolerance) {
        return lastIndexOf(array, doubleToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double[] arrayToFind, double tolerance) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double doubleToFind, int startIndex) {
        return lastIndexOf(array, doubleToFind, startIndex, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double doubleToFind, int startIndex, double tolerance) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        double min = doubleToFind - tolerance;
        double max = doubleToFind + tolerance;

        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double[] arrayToFind, int startIndex) {
        return lastIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(double[] array, double[] arrayToFind, int startIndex, double tolerance) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        double lastMin = arrayToFind[lastIndex] - tolerance;
        double lastMax = arrayToFind[lastIndex] + tolerance;
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && (array[i] < lastMin || array[i] > lastMax)) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (Math.abs(array[j--] - arrayToFind[k--]) > tolerance) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(double[] array, double doubleToFind) {
        return indexOf(array, doubleToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param doubleToFind 要查找的元素
     * @param tolerance 误差
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(double[] array, double doubleToFind, double tolerance) {
        return indexOf(array, doubleToFind, tolerance) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(double[] array, double[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(double[] array, double[] arrayToFind, double tolerance) {
        return indexOf(array, arrayToFind, tolerance) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：float[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float floatToFind) {
        return indexOf(array, floatToFind, 0, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float floatToFind, float tolerance) {
        return indexOf(array, floatToFind, 0, tolerance);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float[] arrayToFind) {
        return indexOf(array, arrayToFind, 0, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float[] arrayToFind, float tolerance) {
        return indexOf(array, arrayToFind, 0, tolerance);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float floatToFind, int startIndex) {
        return indexOf(array, floatToFind, startIndex, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        float min = floatToFind - tolerance;
        float max = floatToFind + tolerance;

        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float[] arrayToFind, int startIndex) {
        return indexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(float[] array, float[] arrayToFind, int startIndex, float tolerance) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        float firstMin = arrayToFind[0] - tolerance;
        float firstMax = arrayToFind[0] + tolerance;
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && (array[i] < firstMin || array[i] > firstMax)) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (Math.abs(array[j++] - arrayToFind[k++]) > tolerance) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float floatToFind) {
        return lastIndexOf(array, floatToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float floatToFind, float tolerance) {
        return lastIndexOf(array, floatToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float[] arrayToFind, float tolerance) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float floatToFind, int startIndex) {
        return lastIndexOf(array, floatToFind, startIndex, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        float min = floatToFind - tolerance;
        float max = floatToFind + tolerance;

        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float[] arrayToFind, int startIndex) {
        return lastIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @param tolerance 误差
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(float[] array, float[] arrayToFind, int startIndex, float tolerance) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        float lastMin = arrayToFind[lastIndex] - tolerance;
        float lastMax = arrayToFind[lastIndex] + tolerance;
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && (array[i] < lastMin || array[i] > lastMax)) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (Math.abs(array[j--] - arrayToFind[k--]) > tolerance) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(float[] array, float floatToFind) {
        return indexOf(array, floatToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param floatToFind 要查找的元素
     * @param tolerance 误差
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(float[] array, float floatToFind, float tolerance) {
        return indexOf(array, floatToFind, tolerance) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(float[] array, float[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param tolerance 误差
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(float[] array, float[] arrayToFind, float tolerance) {
        return indexOf(array, arrayToFind, tolerance) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：boolean[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param booleanToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(boolean[] array, boolean booleanToFind) {
        return indexOf(array, booleanToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(boolean[] array, boolean[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param booleanToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (booleanToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        boolean first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param booleanToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(boolean[] array, boolean booleanToFind) {
        return lastIndexOf(array, booleanToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(boolean[] array, boolean[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param booleanToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (booleanToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        boolean last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param booleanToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(boolean[] array, boolean booleanToFind) {
        return indexOf(array, booleanToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(boolean[] array, boolean[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    // ==========================================================================
    // 在数组中查找一个元素或一个元素序列。
    //
    // 类型：char[]
    // ==========================================================================

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param charToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(char[] array, char charToFind) {
        return indexOf(array, charToFind, 0);
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(char[] array, char[] arrayToFind) {
        return indexOf(array, arrayToFind, 0);
    }

    /**
     * 在数组中查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param charToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (charToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则看作<code>0</code>，超出数组长度的起始索引则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int indexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        char first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // 查找第一个元素
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return INDEX_NOT_FOUND;
            }

            // 已经找到第一个元素，接着找
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // 重新查找第一个元素
                    continue startSearchForFirst;
                }
            }

            // 找到了
            return i;
        }
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param charToFind 要查找的元素
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(char[] array, char charToFind) {
        return lastIndexOf(array, charToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(char[] array, char[] arrayToFind) {
        return lastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * 在数组中从末尾开始查找一个元素。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param charToFind 要查找的元素
     * @param startIndex 起始索引
     * @return 该元素在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (charToFind == array[i]) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 在数组中从末尾开始查找一个元素序列。
     * <p>
     * 如果未找到或数组为<code>null</code>则返回<code>-1</code>。
     * </p>
     * <p>
     * 起始索引小于<code>0</code>则返回<code>-1</code>，超出数组长度的起始索引则从数组末尾开始找。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @param startIndex 起始索引
     * @return 该元素序列在数组中的序号，如果数组为<code>null</code>或未找到，则返回<code>-1</code>。
     */
    public static int lastIndexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return INDEX_NOT_FOUND;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        char last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return INDEX_NOT_FOUND;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * 判断指定对象是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param charToFind 要查找的元素
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(char[] array, char charToFind) {
        return indexOf(array, charToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 判断指定元素序列是否存在于指定数组中。
     * <p>
     * 如果数组为<code>null</code>则返回<code>false</code>。
     * </p>
     * 
     * @param array 要扫描的数组
     * @param arrayToFind 要查找的元素序列
     * @return 如果找到则返回<code>true</code>
     */
    public static boolean contains(char[] array, char[] arrayToFind) {
        return indexOf(array, arrayToFind) != INDEX_NOT_FOUND;
    }

    /*
     * ========================================================================== ==
     */
    /* Clone函数。 */
    /*                                                                              */
    /* 以下方法调用Object.clone方法，进行“浅复制”（shallow copy）。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法只进行“浅复制”，也就是说，数组中的对象本身不会被复制。 另外，此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }

        return (T[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static long[] clone(long[] array) {
        if (array == null) {
            return null;
        }

        return (long[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static int[] clone(int[] array) {
        if (array == null) {
            return null;
        }

        return (int[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static short[] clone(short[] array) {
        if (array == null) {
            return null;
        }

        return (short[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }

        return (byte[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static double[] clone(double[] array) {
        if (array == null) {
            return null;
        }

        return (double[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static float[] clone(float[] array) {
        if (array == null) {
            return null;
        }

        return (float[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static boolean[] clone(boolean[] array) {
        if (array == null) {
            return null;
        }

        return (boolean[]) array.clone();
    }

    /**
     * 复制一个数组。如果数组为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法也不处理多维数组。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     */
    public static char[] clone(char[] array) {
        if (array == null) {
            return null;
        }

        return (char[]) array.clone();
    }

    // ==========================================================================
    // addAll。
    // ==========================================================================

    // FIXME
    public static <T> T[] addAll(T[] array1, T[] array2) {
        if (array1 == null) {
            return (T[]) clone(array2);
        } else if (array2 == null) {
            return (T[]) clone(array1);
        }
        @SuppressWarnings("unchecked")
        T[] joinedArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (ArrayStoreException ase) {
            // Check if problem was due to incompatible types
            /*
             * We do this here, rather than before the copy because: - it would be a wasted check most of the time -
             * safer, in case check turns out to be too strict
             */
            final Class<?> type1 = array1.getClass().getComponentType();
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
                        + type1.getName());
            }
            throw ase; // No, so rethrow original
        }
        return joinedArray;
    }

    public static boolean[] addAll(boolean[] array1, boolean[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        boolean[] joinedArray = new boolean[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static char[] addAll(char[] array1, char[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        char[] joinedArray = new char[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static short[] addAll(short[] array1, short[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        short[] joinedArray = new short[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static int[] addAll(int[] array1, int[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static long[] addAll(long[] array1, long[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static float[] addAll(float[] array1, float[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        float[] joinedArray = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static double[] addAll(double[] array1, double[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        double[] joinedArray = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将数组转换成易于阅读的字符串表示。
     * 
     * <p>
     * 如果数组是<code>null</code>则返回<code>[]</code>，支持多维数组。 如果数组元素为<code>null</code> ，则显示<code>&lt;null&gt;</code>。
     * 
     * <pre>
     * ArrayUtil.toString(null)                              = &quot;[]&quot;
     * ArrayUtil.toString(new int[] {1, 2, 3})               = &quot;[1, 2, 3]&quot;
     * ArrayUtil.toString(new boolean[] {true, false, true}) = &quot;[true, false, true]&quot;
     * ArrayUtil.toString(new Object[] {
     *                       {1, 2, 3},  // 嵌套数组
     *                       hello,      // 嵌套非数组
     *                       null,       // 嵌套null
     *                       {},         // 嵌套空数组
     *                       {2, 3, 4}   // 嵌套数组
     *                    })                                 = &quot;[[1, 2, 3], hello, &lt;null&gt;, [], [2, 3, 4]]&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param array 要转换的数组
     * 
     * @return 字符串表示，<code>"[]"</code>表示空数组或<code>null</code>
     */
    // // FIXME bug
    // public static String toString(Object array) {
    // return toString(array, "[]", "<null>");
    // }

    public static String toString(Object array) {
        if (ObjectUtil.isEmpty(array)) {
            return null;
        }
        Class<?> type = array.getClass();
        if (type.isArray()) {
            if (type == char[].class) {
                return toString((char[]) array);
            }

            if (type == int[].class) {
                return toString((int[]) array);
            }
            if (type == long[].class) {
                return toString((long[]) array);
            }
            if (type == byte[].class) {
                return toString((byte[]) array);
            }
            if (type == float[].class) {
                return toString((float[]) array);
            }
            if (type == double[].class) {
                return toString((double[]) array);
            }
            if (type == short[].class) {
                return toString((short[]) array);
            }
            if (type == boolean[].class) {
                return toString((boolean[]) array);
            }
            return toString((Object[]) array);
        }

        return array.toString();
    }

    private static String toString(int[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(long[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(short[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(char[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(byte[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(boolean[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(float[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(double[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(array[i]);
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    private static String toString(Object[] array) {
        int iMax = array.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0;; i++) {
            builder.append(String.valueOf(array[i]));
            if (i == iMax) {
                return builder.toString();
            }
            builder.append(",");
        }
    }

    /**
     * 将数组转换成易于阅读的字符串表示。
     * 
     * <p>
     * 如果数组是<code>null</code>则返回指定字符串，支持多维数组。 如果数组元素为<code>null</code>，则显示 <code>&lt;null&gt;</code>。
     * 
     * <pre>
     * ArrayUtil.toString(null, &quot;null&quot;)                              = &quot;null&quot;
     * ArrayUtil.toString(new int[] {1, 2, 3}, &quot;null&quot;)               = &quot;[1, 2, 3]&quot;
     * ArrayUtil.toString(new boolean[] {true, false, true}, &quot;null&quot;) = &quot;[true, false, true]&quot;
     * ArrayUtil.toString(new Object[] {
     *                       {1, 2, 3},  // 嵌套数组
     *                       hello,      // 嵌套非数组
     *                       null,       // 嵌套null
     *                       {},         // 嵌套空数组
     *                       {2, 3, 4}   // 嵌套数组
     *                    }, &quot;null&quot;)                                 = &quot;[[1, 2, 3], hello, &lt;null&gt;, [], [2, 3, 4]]&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param array 要转换的数组
     * @param nullArrayStr 如果数组是<code>null</code>，则返回此字符串
     * 
     * @return 字符串表示，或返回指定字符串表示<code>null</code>
     */
    public static String toString(Object array, String nullArrayStr) {
        return toString(array, nullArrayStr, "<null>");
    }

    /**
     * 将数组转换成易于阅读的字符串表示。
     * 
     * <p>
     * 如果数组是<code>null</code>则返回指定字符串，支持多维数组。 如果数组元素为<code>null</code>，则显示指定字符串。
     * 
     * <pre>
     * ArrayUtil.toString(null, &quot;null&quot;, &quot;NULL&quot;)                              = &quot;null&quot;
     * ArrayUtil.toString(new int[] {1, 2, 3}, &quot;null&quot;, &quot;NULL&quot;)               = &quot;[1, 2, 3]&quot;
     * ArrayUtil.toString(new boolean[] {true, false, true}, &quot;null&quot;, &quot;NULL&quot;) = &quot;[true, false, true]&quot;
     * ArrayUtil.toString(new Object[] {
     *                       {1, 2, 3},  // 嵌套数组
     *                       hello,      // 嵌套非数组
     *                       null,       // 嵌套null
     *                       {},         // 嵌套空数组
     *                       {2, 3, 4}   // 嵌套数组
     *                    }, &quot;null&quot;, &quot;NULL&quot;)                                 = &quot;[[1, 2, 3], hello, NULL, [], [2, 3, 4]]&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param array 要转换的数组
     * @param nullArrayStr 如果数组是<code>null</code>，则返回此字符串
     * @param nullElementStr 如果数组中的元素为<code>null</code>，则返回此字符串
     * 
     * @return 字符串表示，或返回指定字符串表示<code>null</code>
     */
    public static String toString(Object array, String nullArrayStr, String nullElementStr) {
        if (array == null) {
            return nullArrayStr;
        }

        StringBuilder builder = new StringBuilder();

        toString(builder, array, nullArrayStr, nullElementStr);

        return builder.toString();
    }

    /**
     * 将数组转换成易于阅读的字符串表示。<code>null</code>将被看作空数组。 支持多维数组。FIXME
     * 
     * @param builder 将转换后的字符串加入到这个<code>StringBuilder</code>中
     * @param array 要转换的数组
     * @param nullArrayStr 如果数组是<code>null</code>，则返回此字符串
     * @param nullElementStr 如果数组中的元素为<code>null</code>，则返回此字符串
     */
    private static void toString(StringBuilder builder, Object array, String nullArrayStr, String nullElementStr) {
        if (array == null) {
            builder.append(nullElementStr);
            return;
        }

        if (!array.getClass().isArray()) {
            builder.append(ObjectUtil.toString(array, nullElementStr));
            return;
        }

        builder.append('[');

        // array为数组
        if (long[].class.isInstance(array)) {
            long[] longArray = (long[]) array;
            int length = longArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(longArray[i]);
            }
        } else if (int[].class.isInstance(array)) {
            int[] intArray = (int[]) array;
            int length = intArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(intArray[i]);
            }
        } else if (short[].class.isInstance(array)) {
            short[] shortArray = (short[]) array;
            int length = shortArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(shortArray[i]);
            }
        } else if (byte[].class.isInstance(array)) {
            byte[] byteArray = (byte[]) array;
            int length = byteArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                } else {
                    builder.append("0x");
                }

                String hexStr = Integer.toHexString(0xFF & byteArray[i]).toUpperCase();

                if (hexStr.length() == 0) {
                    builder.append("00");
                } else if (hexStr.length() == 1) {
                    builder.append("0");
                }

                builder.append(hexStr);
            }
        } else if (double[].class.isInstance(array)) {
            double[] doubleArray = (double[]) array;
            int length = doubleArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(doubleArray[i]);
            }
        } else if (float[].class.isInstance(array)) {
            float[] floatArray = (float[]) array;
            int length = floatArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(floatArray[i]);
            }
        } else if (boolean[].class.isInstance(array)) {
            boolean[] booleanArray = (boolean[]) array;
            int length = booleanArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(booleanArray[i]);
            }
        } else if (char[].class.isInstance(array)) {
            char[] charArray = (char[]) array;
            int length = charArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(charArray[i]);
            }
        } else {
            Object[] objectArray = (Object[]) array;
            int length = objectArray.length;

            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                toString(builder, objectArray[i], nullArrayStr, nullElementStr);
            }
        }

        builder.append(']');
    }

    /**
     * 判断两个数组的相容性, 如果一个被另一个包含，即为相容
     * 
     * @param <T>
     * @param arrayA 数组A
     * @param arrayB 数组B
     * @return 如果相容则返回<code>true</code>，否则返回<code>false</code>
     */
    public static <T> boolean isCompatible(T[] arrayA, T[] arrayB) {
        if (arrayA == null) {
            return arrayB == null;
        }
        if (arrayB == null) {
            return false;
        }
        if (arrayA.length > arrayB.length) {
            final T[] tmp = arrayA;
            arrayA = arrayB;
            arrayB = tmp;
        }
        boolean flag;
        for (final T a : arrayA) {
            flag = false;
            for (final T b : arrayB) {
                if (a.equals(b)) {
                    flag = true;
                }
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    // ==========================================================================
    // resize。
    // ==========================================================================

    public static <T> T[] resize(T[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(componentType, newSize);
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static String[] resize(String[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        String[] result = new String[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static byte[] resize(byte[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        byte[] result = new byte[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static char[] resize(char[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        char[] result = new char[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static short[] resize(short[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        short[] result = new short[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static int[] resize(int[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        int[] result = new int[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static long[] resize(long[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        long[] result = new long[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static float[] resize(float[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        float[] result = new float[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static double[] resize(double[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        double[] result = new double[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    public static boolean[] resize(boolean[] buffer, int newSize) {
        if (buffer == null) {
            return null;
        }

        boolean[] result = new boolean[newSize];
        System.arraycopy(buffer, 0, result, 0, buffer.length >= newSize ? newSize : buffer.length);
        return result;
    }

    // ==========================================================================
    // toStringArray。
    // ==========================================================================

    public static String[] toStringArray(Object[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = StringUtil.toString(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(String[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(byte[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(char[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(short[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(int[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(long[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(float[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(double[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(boolean[] array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = String.valueOf(array[i]);
        }
        return result;
    }

    public static String[] toStringArray(Object value) {
        if (value == null) {
            return new String[0];
        }
        Class<?> type = value.getClass();

        if (!type.isArray()) {
            return new String[] { value.toString() };
        }

        Class<?> componentType = type.getComponentType();

        if (componentType.isPrimitive()) {
            if (componentType == int.class) {
                return toStringArray((int[]) value);
            }
            if (componentType == long.class) {
                return toStringArray((long[]) value);
            }
            if (componentType == double.class) {
                return toStringArray((double[]) value);
            }
            if (componentType == float.class) {
                return toStringArray((float[]) value);
            }
            if (componentType == boolean.class) {
                return toStringArray((boolean[]) value);
            }
            if (componentType == short.class) {
                return toStringArray((short[]) value);
            }
            if (componentType == byte.class) {
                return toStringArray((byte[]) value);
            }
            throw new IllegalArgumentException();
        }

        return toStringArray((Object[]) value);
    }

    // ==========================================================================
    // change types。FIXME
    // ==========================================================================

    public static boolean[] charToBoolean(char[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] intToBoolean(int[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] longToBoolean(long[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] doubleToBoolean(double[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] floatToBoolean(float[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] byteToBoolean(byte[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    public static boolean[] shortToBoolean(short[] values) {
        if (values == null) {
            return null;
        }

        boolean[] results = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] != 0);
        }
        return results;
    }

    // ==========================================================================

    public static byte[] charToByte(char[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] intToByte(int[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] longToByte(long[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] doubleToByte(double[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] floatToByte(float[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] shortToByte(short[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) values[i];
        }
        return results;
    }

    public static byte[] booleanToByte(boolean[] values) {
        if (values == null) {
            return null;
        }

        byte[] results = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (byte) (values[i] == true ? 1 : 0);
        }
        return results;
    }

    // ==========================================================================

    public static char[] intToChar(int[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    public static char[] longToChar(long[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    public static char[] doubleToChar(double[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    public static char[] floatToChar(float[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    public static char[] shortToChar(short[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    public static char[] booleanToChar(boolean[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] == true ? '1' : '0');
        }
        return results;
    }

    public static char[] byteToChar(byte[] values) {
        if (values == null) {
            return null;
        }

        char[] results = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (char) values[i];
        }
        return results;
    }

    // ==========================================================================

    public static double[] intToDouble(int[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static double[] longToDouble(long[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static double[] charToDouble(char[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static double[] floatToDouble(float[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static double[] shortToDouble(short[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static double[] booleanToDouble(boolean[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] == true ? 1 : 0);
        }
        return results;
    }

    public static double[] byteToDouble(byte[] values) {
        if (values == null) {
            return null;
        }

        double[] results = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    // ==========================================================================

    public static float[] intToFloat(int[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static float[] longToFloat(long[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static float[] charToFloat(char[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static float[] doubleToFloat(double[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (float) values[i];
        }
        return results;
    }

    public static float[] shortToFloat(short[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static float[] booleanToFloat(boolean[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] == true ? 1 : 0);
        }
        return results;
    }

    public static float[] byteToFloat(byte[] values) {
        if (values == null) {
            return null;
        }

        float[] results = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    // ==========================================================================

    public static int[] floatToInt(float[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (int) values[i];
        }
        return results;
    }

    public static int[] longToInt(long[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (int) values[i];
        }
        return results;
    }

    public static int[] charToInt(char[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static int[] doubleToInt(double[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (int) values[i];
        }
        return results;
    }

    public static int[] shortToInt(short[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static int[] booleanToInt(boolean[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] == true ? 1 : 0);
        }
        return results;
    }

    public static int[] byteToInt(byte[] values) {
        if (values == null) {
            return null;
        }

        int[] results = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    // ==========================================================================

    public static long[] floatToLong(float[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (long) values[i];
        }
        return results;
    }

    public static long[] intToLong(int[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static long[] charToLong(char[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static long[] doubleToLong(double[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (long) values[i];
        }
        return results;
    }

    public static long[] shortToLong(short[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static long[] booleanToLong(boolean[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (values[i] == true ? 1 : 0);
        }
        return results;
    }

    public static long[] byteToLong(byte[] values) {
        if (values == null) {
            return null;
        }

        long[] results = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    // ==========================================================================

    public static short[] floatToShort(float[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    public static short[] intToShort(int[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    public static short[] charToShort(char[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    public static short[] doubleToShort(double[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    public static short[] longToShort(long[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    public static short[] booleanToShort(boolean[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) (values[i] == true ? 1 : 0);
        }
        return results;
    }

    public static short[] byteToShort(byte[] values) {
        if (values == null) {
            return null;
        }

        short[] results = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = (short) values[i];
        }
        return results;
    }

    // ==========================================================================
    // primitive to String
    // ==========================================================================

    public static String[] floatToString(float[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] intToString(int[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] charToString(char[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] doubleToString(double[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] longToString(long[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] booleanToString(boolean[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] byteToString(byte[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    public static String[] shortToString(short[] values) {
        if (values == null) {
            return null;
        }

        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = String.valueOf(values[i]);
        }
        return results;
    }

    // ==========================================================================
    // primitive and wrapper convert
    // ==========================================================================

    public static <F, T> T[] primitiveToWrapper(Object froms) {
        if (froms == null) {
            return null;
        }

        Class<?> type = froms.getClass().getComponentType();
        if (!type.isPrimitive()) {
            return null;
        }

        PrimitiveArray transformTo = PrimitiveArray.find(froms.getClass());
        if (transformTo == null) {
            return null;
        }

        int size = Array.getLength(froms);
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(ClassUtil.getWrapperTypeIfPrimitive(type), size);

        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T value = (T) Array.get(froms, i);
            result[i] = value;
        }

        return result;
    }

    // FIXME
    public static <F, T> T[] primitiveToWrapper(F[] froms) {
        if (froms == null) {
            return null;
        }

        Class<?> type = froms.getClass().getComponentType();
        if (!type.isPrimitive()) {
            return null;
        }

        PrimitiveArray transformTo = PrimitiveArray.find(froms.getClass());
        if (transformTo == null) {
            return null;
        }

        int size = froms.length;
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(ClassUtil.getWrapperTypeIfPrimitive(type), size);
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T value = (T) froms[i];
            result[i] = value;
        }

        return result;
    }

    public static <F, T> T[] wrapperToPrimitive(F[] froms) {
        if (froms == null) {
            return null;
        }

        Class<?> type = froms.getClass().getComponentType();
        Class<?> primitiveClass = ClassUtil.getPrimitiveType(type);
        if (primitiveClass == null || !primitiveClass.isPrimitive()) {
            return null;
        }

        PrimitiveArray transformTo = PrimitiveArray.find(froms.getClass());
        if (transformTo == null) {
            return null;
        }

        int size = froms.length;
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(primitiveClass, size);
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T value = (T) froms[i];
            result[i] = value;
        }

        return result;
    }

    // ==========================================================================
    // create
    // ==========================================================================

    public static <T> T[] create(int length, Class<T> type) {
        if (type == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(type, length);

        return array;
    }

    // ==========================================================================
    // subarray
    // ==========================================================================

    public static <T> T[] subarray(T[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Class<T> componentType = (Class<T>) buffer.getClass().getComponentType();

        return subarray(buffer, offset, length, componentType);
    }

    public static <T> T[] subarray(T[] buffer, int offset, int length, Class<T> componentType) {
        if (buffer == null) {
            return null;
        }

        T[] result = create(length, componentType);
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static String[] subarray(String[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        String result[] = new String[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static byte[] subarray(byte[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        byte result[] = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static char[] subarray(char[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        char result[] = new char[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static short[] subarray(short[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        short result[] = new short[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static int[] subarray(int[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int result[] = new int[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static long[] subarray(long[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        long result[] = new long[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static float[] subarray(float[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        float result[] = new float[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static double[] subarray(double[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        double result[] = new double[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    public static boolean[] subarray(boolean[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        boolean result[] = new boolean[length];
        System.arraycopy(buffer, offset, result, 0, length);

        return result;
    }

    // ==========================================================================
    // remove。
    // ==========================================================================
    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] array, int index) {
        return (T[]) remove((Object) array, index);
    }

    public static Object[] removeElement(Object[] array, Object element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static boolean[] remove(boolean[] array, int index) {
        return (boolean[]) remove((Object) array, index);
    }

    public static boolean[] removeElement(boolean[] array, boolean element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static byte[] remove(byte[] array, int index) {
        return (byte[]) remove((Object) array, index);
    }

    public static byte[] removeElement(byte[] array, byte element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static char[] remove(char[] array, int index) {
        return (char[]) remove((Object) array, index);
    }

    public static char[] removeElement(char[] array, char element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static double[] remove(double[] array, int index) {
        return (double[]) remove((Object) array, index);
    }

    public static double[] removeElement(double[] array, double element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static float[] remove(float[] array, int index) {
        return (float[]) remove((Object) array, index);
    }

    public static float[] removeElement(float[] array, float element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static int[] remove(int[] array, int index) {
        return (int[]) remove((Object) array, index);
    }

    public static int[] removeElement(int[] array, int element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static long[] remove(long[] array, int index) {
        return (long[]) remove((Object) array, index);
    }

    public static long[] removeElement(long[] array, long element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    public static short[] remove(short[] array, int index) {
        return (short[]) remove((Object) array, index);
    }

    public static short[] removeElement(short[] array, short element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        return remove(array, index);
    }

    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    public static <T> T[] remove(T[] buffer, int offset, int length) {
        return remove(buffer, offset, length, null);
    }

    public static <T> T[] remove(T[] buffer, int offset, int length, Class<?> componentType) {
        if (buffer == null) {
            return null;
        }

        if (componentType == null) {
            componentType = buffer.getClass().getComponentType();
        }

        int len = buffer.length - length;
        @SuppressWarnings({ "unchecked" })
        T[] result = (T[]) Array.newInstance(componentType, len);
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static String[] remove(String[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        String result[] = new String[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static byte[] remove(byte[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        byte result[] = new byte[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static char[] remove(char[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        char result[] = new char[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static short[] remove(short[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        short result[] = new short[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static int[] remove(int[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        int result[] = new int[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static long[] remove(long[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        long result[] = new long[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static float[] remove(float[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        float result[] = new float[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static double[] remove(double[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        double result[] = new double[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static boolean[] remove(boolean[] buffer, int offset, int length) {
        if (buffer == null) {
            return null;
        }

        int len = buffer.length - length;
        boolean result[] = new boolean[len];
        System.arraycopy(buffer, 0, result, 0, offset);
        System.arraycopy(buffer, offset + length, result, offset, len - offset);
        return result;
    }

    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }

        return Array.getLength(array);
    }
}
