/**
 * 
 */
package com.baidu.unbiz.common.diagnostic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.JVMUtil;

/**
 * 计算对象所占用字节数
 * <p>
 * 一个引用：4字节
 * 
 * <p>
 * 一个Object：8字节
 * 
 * <p>
 * 一个Integer：16字节 == (8 + 4) / 8 * 8
 * 
 * <p>
 * 一个int：4字节
 * 
 * <p>
 * 长度为0的数组大小：JRo64=24, Sun32=12
 * 
 * <p>
 * 引用大小，如Object = null： JRo64=JRo32=4, Sun32=4, Sun64=8
 * 
 * <p>
 * 无成员的对象大小，如new Object();： JRo32=JRo64=8, Sun32=8, Sun64=16
 * 
 * <p>
 * new byte[0]: JRo32=JRo64=8+8 Sun32=8+4, Sun64=16+8
 * 
 * <p>
 * 长度l的byte数组：(l+19)/8*8
 * 
 * <p>
 * 长度l的char/short数组：(l*2+19)/8*8 == (l+9)/4*8
 * 
 * <p>
 * 长度l的String：(l+1)/4*8+40
 * 
 * <p>
 * 长度l的int数组：(l*4+19)/8*8 ==(l+4)/2*8
 * 
 * <p>
 * 长度l的long数组：(l*8+19)/8*8 == (l+2)*8
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月11日 上午11:48:20
 */
public class Occupy {

    private static final String sunVendorURL = "http://java.sun.com/";

    private static final String jRockitVendorURL = "http://www.oracle.com/";

    // 这8个方法不写不行，否则occupyof(int x)会自动重载到occupyof(Object o),并且无法在方法中判断

    public static int occupyof(boolean variable) {
        return 1;
    }

    public static int occupyof(byte variable) {
        return 1;
    }

    public static int occupyof(short variable) {
        return 2;
    }

    public static int occupyof(char variable) {
        return 2;
    }

    public static int occupyof(int variable) {
        return 4;
    }

    public static int occupyof(float variable) {
        return 4;
    }

    public static int occupyof(long variable) {
        return 8;
    }

    public static int occupyof(double variable) {
        return 8;
    }

    // go

    private final byte NULL_REFERENCE_SIZE;
    private final byte EMPTY_OBJECT_SIZE;
    private final byte EMPTY_ARRAY_VAR_SIZE;
    private final String name;

    public Occupy(byte nullReferenceSize, byte emptyObjectSize, byte emptyArrayVarSize) {
        this.NULL_REFERENCE_SIZE = nullReferenceSize;
        this.EMPTY_OBJECT_SIZE = emptyObjectSize;
        this.EMPTY_ARRAY_VAR_SIZE = emptyArrayVarSize;
        this.name = "unknown";
    }

    public Occupy(byte nullReferenceSize, byte emptyObjectSize, byte emptyArrayVarSize, String name) {
        this.NULL_REFERENCE_SIZE = nullReferenceSize;
        this.EMPTY_OBJECT_SIZE = emptyObjectSize;
        this.EMPTY_ARRAY_VAR_SIZE = emptyArrayVarSize;
        this.name = name;
    }

    public static Occupy forJRockitVM() {
        return new Occupy((byte) 4, (byte) 8, (byte) 8, "JRockitVM");
    }

    public static Occupy forSun32BitsVM() {
        return new Occupy((byte) 4, (byte) 8, (byte) 4, "Sun32BitsVM");
    }

    public static Occupy forSun64BitsVM() {
        return new Occupy((byte) 8, (byte) 16, (byte) 8, "Sun64BitsVM");
    }

    public static Occupy forDetectedVM() {
        String vendorURL = JVMUtil.getJavaRuntimeInfo().getVendorURL();
        if (jRockitVendorURL.equals(vendorURL)) {
            return forJRockitVM();
        }
        if (sunVendorURL.equals(vendorURL)) {
            String sunArchDataModel = JVMUtil.getJavaRuntimeInfo().getSunArchDataModel();
            if ("32".equals(sunArchDataModel)) {
                return forSun32BitsVM();
            }
            if ("64".equals(sunArchDataModel)) {
                return forSun64BitsVM();
            }
        }

        return null;

    }

    private static class ref {

        public ref(Object obj) {
            this.obj = obj;
        }

        final Object obj;

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ref) && ((ref) obj).obj == this.obj;
        }

        @Override
        public int hashCode() {
            return obj.hashCode();
        }

    }

    private List<ref> dedup = CollectionUtil.createArrayList();

    private Map<Object, Object> identityMap = CollectionUtil.createIdentityHashMap();

    /**
     * 
     * 对象占用的内存空间，对象占用空间与对象的大小并不相等，
     * <p
     * .就好象Windows下文件一样(大小为1字节时占用空间4k)
     * 
     * @param object
     * 
     * @return 占用的内存空间
     */

    public int occupyof(Object object) {
        dedup.clear();
        return occupyof0(object);
    }

    private int occupyof0(Object object) {
        if (object == null) {
            return 0;
        }

        ref r = new ref(object);
        if (dedup.contains(r))
            return 0;
        dedup.add(r);
        int varSize = 0;// 对象中的值类型、引用类型变量大小
        int objSize = 0;// 对象中的引用类型指向的对象实例的大小
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            // System.out.println(clazz);
            if (clazz.isArray()) {// 当前对象的数组
                varSize += EMPTY_ARRAY_VAR_SIZE;
                Class<?> componentType = clazz.getComponentType();
                if (componentType.isPrimitive()) {// 当前数组是原生类型的数组
                    varSize += lengthOfPrimitiveArray(object) * sizeofPrimitiveClass(componentType);
                    return occupyOfSize(EMPTY_OBJECT_SIZE, varSize, 0);

                }
                Object[] array = (Object[]) object;
                varSize += NULL_REFERENCE_SIZE * array.length;// 当前数组有length个引用，每个占用4字节
                for (Object o : array) {
                    objSize += occupyof0(o);
                }

                return occupyOfSize(EMPTY_OBJECT_SIZE, varSize, objSize);

            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || clazz != field.getDeclaringClass()) {
                    continue;
                }

                Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    varSize += sizeofPrimitiveClass(type);
                }

                else {
                    varSize += NULL_REFERENCE_SIZE;// 一个引用型变量占用4个字节
                    try {
                        field.setAccessible(true);// 可以访问非public类型的变量
                        objSize += occupyof0(field.get(object));
                    } catch (Exception e) {
                        objSize += occupyofConstructor(object, field);
                    }
                }
            }
        }

        return occupyOfSize(EMPTY_OBJECT_SIZE, varSize, objSize);

    }

    public static int sizeof(boolean variable) {
        return 1;
    }

    public static int sizeof(byte variable) {
        return 1;
    }

    public static int sizeof(short variable) {
        return 2;
    }

    public static int sizeof(char variable) {
        return 2;
    }

    public static int sizeof(int variable) {
        return 4;
    }

    public static int sizeof(float variable) {
        return 4;
    }

    public static int sizeof(long variable) {
        return 8;
    }

    public static int sizeof(double variable) {
        return 8;
    }

    public int sizeof(Object object) {
        identityMap.clear();
        return sizeof0(object);
    }

    /**
     * 
     * 对象的大小
     * 
     * @param object
     * 
     * @return 对象的大小
     */

    private int sizeof0(Object object) {
        if (object == null) {
            return 0;
        }

        Class<?> clazz = object.getClass();
        if (!clazz.isPrimitive()) {
            identityMap.put(object, null);
        }
        int size = EMPTY_OBJECT_SIZE;
        if (clazz.isArray()) {
            return sizeofArray(clazz, object, size);
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;// 类成员不计
            }

            Class<?> type = field.getType();
            if (type.isPrimitive())
                size += sizeofPrimitiveClass(type);
            else {
                size += 4;// 一个引用型变量占用4个字节
                try {
                    field.setAccessible(true);// 可以访问非public类型的变量
                    // FIXME 存在死循环,fixed
                    Object value = field.get(object);
                    if (value != null && !identityMap.containsKey(value)) {
                        identityMap.put(value, null);
                        size += sizeof0(value);
                    }
                } catch (Exception e) {
                    size += sizeofConstructor(object, field);
                }
            }
        }

        return size;

    }

    private int sizeofArray(Class<?> clazz, Object object, int initSize) {
        initSize += EMPTY_ARRAY_VAR_SIZE;// length变量是int型
        Class<?> componentType = clazz.getComponentType();
        if (componentType.isPrimitive()) {
            return initSize + lengthOfPrimitiveArray(object) * sizeofPrimitiveClass(componentType);
        }

        Object[] array = (Object[]) object;
        initSize += 4 * array.length;
        for (Object o : array) {
            initSize += sizeof0(o);
        }

        return initSize;
    }

    private static int occupyofConstructor(Object object, Field field) {
        throw new UnsupportedOperationException("field type Constructor not accessible: " + object.getClass()
                + " field:" + field);
    }

    private static int sizeofConstructor(Object object, Field field) {
        throw new UnsupportedOperationException("field type Constructor not accessible: " + object.getClass()
                + " field:" + field);
    }

    /**
     * 
     * 对象的大小 和 占用空间并不相等，就好象Windows下文件一样(大小为1字节时占用空间4k)
     * 
     * 对象占用空间的增长以8个字节为单位，占用空间=大小对8的无条件进位法，
     * 
     * 即occupy = (size + 8 - 1) / 8 * 8; 例如：
     * 
     * 大小8字节：占用8字节，(new Object()就是占用8字节)
     * 
     * 大小9字节：占用16字节
     * 
     * 大小16字节：占用16字节
     * 
     * 大小17字节：占用24字节
     * 
     * @param size 大小，以字节为单位
     * 
     * @return 占用空间
     */

    private static int occupyOfSize(int size) {
        return (size + 7) / 8 * 8;
    }

    private static int occupyOfSize(int selfSize, int varsSize, int objsSize) {
        // System.out.println("self=" + selfSize + " vars=" + varsSize +
        // " objs=" + objsSize);
        return occupyOfSize(selfSize) + occupyOfSize(varsSize) + objsSize;
    }

    private static int sizeofPrimitiveClass(Class<?> clazz) {
        return clazz == boolean.class || clazz == byte.class ? 1 : clazz == char.class || clazz == short.class ? 2
                : clazz == int.class || clazz == float.class ? 4 : 8;
    }

    private static int lengthOfPrimitiveArray(Object object) {

        Class<?> clazz = object.getClass();
        return clazz == boolean[].class ? ((boolean[]) object).length
                : clazz == byte[].class ? ((byte[]) object).length : clazz == char[].class ? ((char[]) object).length
                        : clazz == short[].class ? ((short[]) object).length
                                : clazz == int[].class ? ((int[]) object).length
                                        : clazz == float[].class ? ((float[]) object).length
                                                : clazz == long[].class ? ((long[]) object).length
                                                        : ((double[]) object).length;

    }

    public String toString() {
        return name;
    }

}
