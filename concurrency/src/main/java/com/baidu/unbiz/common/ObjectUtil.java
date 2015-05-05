package com.baidu.unbiz.common;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 有关<code>Object</code>处理的工具类。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理<code>null</code>，而不会抛出<code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月20日 上午5:42:51
 */
public abstract class ObjectUtil {

    public static final String SERIAL_VERSION_UID = "serialVersionUID";

    // ==========================================================================
    // 默认值函数。
    //
    // 当对象为null时，将对象转换成指定的默认对象。
    // ==========================================================================

    /**
     * 如果对象为<code>null</code>，则返回指定默认对象，否则返回对象本身。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     * 
     * @param object 要测试的对象
     * @param defaultValue 默认值
     * @return 对象本身或默认对象
     */
    public static <T, S extends T> T defaultIfNull(T object, S defaultValue) {
        return object == null ? defaultValue : object;
    }

    // ==========================================================================
    // 比较函数。
    //
    // 以下方法用来比较两个对象的值或类型是否相同。
    // ==========================================================================

    /**
     * 比较两个对象是否完全相等。
     * <p>
     * 此方法可以正确地比较多维数组。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.equals(null, null)                  = true
     * ObjectUtil.equals(null, "")                    = false
     * ObjectUtil.equals("", null)                    = false
     * ObjectUtil.equals("", "")                      = true
     * ObjectUtil.equals(Boolean.TRUE, null)          = false
     * ObjectUtil.equals(Boolean.TRUE, "true")        = false
     * ObjectUtil.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtil.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     * <p/>
     * </p>
     * 
     * @param object1 对象1
     * @param object2 对象2
     * @return 如果相等, 则返回<code>true</code>
     */
    public static boolean isEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }

        if (object1 == null || object2 == null) {
            return false;
        }

        if (!object1.getClass().equals(object2.getClass())) {
            return false;
        }

        if (object1 instanceof Object[]) {
            return Arrays.deepEquals((Object[]) object1, (Object[]) object2);
        }
        if (object1 instanceof int[]) {
            return Arrays.equals((int[]) object1, (int[]) object2);
        }
        if (object1 instanceof long[]) {
            return Arrays.equals((long[]) object1, (long[]) object2);
        }
        if (object1 instanceof short[]) {
            return Arrays.equals((short[]) object1, (short[]) object2);
        }
        if (object1 instanceof byte[]) {
            return Arrays.equals((byte[]) object1, (byte[]) object2);
        }
        if (object1 instanceof double[]) {
            return Arrays.equals((double[]) object1, (double[]) object2);
        }
        if (object1 instanceof float[]) {
            return Arrays.equals((float[]) object1, (float[]) object2);
        }
        if (object1 instanceof char[]) {
            return Arrays.equals((char[]) object1, (char[]) object2);
        }
        if (object1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) object1, (boolean[]) object2);
        }
        return object1.equals(object2);
    }

    // ==========================================================================
    // Hash code函数。
    //
    // 以下方法用来取得对象的hash code。
    // ==========================================================================

    /**
     * 取得对象的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 此方法可以正确地处理多维数组。
     * </p>
     * 
     * @param object 对象
     * @return hash值
     */
    public static int hashCode(Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Object[]) {
            return Arrays.deepHashCode((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.hashCode((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.hashCode((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.hashCode((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.hashCode((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.hashCode((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.hashCode((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.hashCode((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) object);
        } else {
            return object.hashCode();
        }
    }

    // ==========================================================================
    // 取得对象的identity。
    // ==========================================================================

    /**
     * 取得对象的原始的hash值, 如果对象为<code>null</code>, 则返回<code>0</code>。
     * <p>
     * 该方法使用<code>System.identityHashCode</code>来取得hash值，该值不受对象本身的 <code>hashCode</code>方法的影响。
     * </p>
     * 
     * @param object 对象
     * @return hash值
     */
    public static int identityHashCode(Object object) {
        return object == null ? 0 : System.identityHashCode(object);
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.identityToString(null)          = null
     * ObjectUtil.identityToString("")            = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE)  = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0])    = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0]) = "java.lang.Object[]@7fa"
     * </pre>
     * 
     * @param object 对象
     * @return 对象的identity，如果对象是<code>null</code>，则返回<code>null</code>
     */
    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * 取得对象自身的identity，如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.identityToString(null, "NULL")            = "NULL"
     * ObjectUtil.identityToString("", "NULL")              = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE, "NULL")    = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0], "NULL")      = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0], "NULL")   = "java.lang.Object[]@7fa"
     * </pre>
     * 
     * @param object 对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的identity，如果对象是<code>null</code>，则返回指定字符串
     */
    public static String identityToString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出——追加到
     * <code>Appendable</code>中。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.appendIdentityToString(buf, null)          = null
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append("int[]@7fa")
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append("java.lang.Object[]@7fa")
     * </pre>
     * 
     * @param buffer <code>Appendable</code>对象
     * @param object 对象
     * @return <code>Appendable</code>对象，如果对象为<code>null</code>，则输出 <code>"null"</code>
     */
    public static <A extends Appendable> A appendIdentityToString(A buffer, Object object) {
        Assert.assertNotNull(buffer, "appendable");

        try {
            if (object == null) {
                buffer.append("null");
            } else {
                buffer.append(ClassUtil.getFriendlyClassNameForObject(object));
                buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return buffer;
    }

    // ==========================================================================
    // toString方法。
    // ==========================================================================

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回空字符串 <code>""</code>。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.toString(null)         = ""
     * ObjectUtil.toString("")           = ""
     * ObjectUtil.toString("bat")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE) = "true"
     * ObjectUtil.toString([1, 2, 3])    = "[1, 2, 3]"
     * </pre>
     * 
     * @param object 对象
     * @return 对象的<code>toString()</code>的返回值，或空字符串<code>""</code>
     */
    public static String toString(Object object) {
        return toString(object, Emptys.EMPTY_STRING);
    }

    /**
     * 取得对象的<code>toString()</code>的值，如果对象为<code>null</code>，则返回指定字符串。
     * <p/>
     * 
     * <pre>
     * ObjectUtil.toString(null, null)           = null
     * ObjectUtil.toString(null, "null")         = "null"
     * ObjectUtil.toString("", "null")           = ""
     * ObjectUtil.toString("bat", "null")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE, "null") = "true"
     * ObjectUtil.toString([1, 2, 3], "null")    = "[1, 2, 3]"
     * </pre>
     * 
     * @param object 对象
     * @param nullStr 如果对象为<code>null</code>，则返回该字符串
     * @return 对象的<code>toString()</code>的返回值，或指定字符串
     */
    public static String toString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        } else if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        } else {
            return object.toString();
        }
    }

    /*
     * ========================================================================== ==
     */
    /* 比较对象的类型。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 检查两个对象是否属于相同类型。<code>null</code>将被看作任意类型。
     * 
     * @param object1 对象1
     * @param object2 对象2
     * 
     * @return 如果两个对象有相同的类型，则返回<code>true</code>
     */
    public static boolean isSameType(Object object1, Object object2) {
        if ((object1 == null) || (object2 == null)) {
            return true;
        }

        return object1.getClass().equals(object2.getClass());
    }

    /*
     * ========================================================================== ==
     */
    /* null或empty方法。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 判断对象是否是空
     * 
     * @param object 传入对象
     * 
     * @return 如果<code>object.toString()</code>为<code>""</code>、 <code>null</code> 或者空数组, 则返回<code>true</code>
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String) {
            return StringUtil.isEmpty(object.toString());
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }

        return false;
    }

    /**
     * 判断对象是否不为空
     * 
     * @param object 传入对象
     * 
     * @return 如果<code>obj.toString()</code>不为<code>""</code>且不为 <code>null</code>, 则返回<code>true</code>
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * 判断对象是否为<code>null</code>
     * 
     * @param object 传入对象
     * @return 为<code>null</code>则返还<code>true</code>
     */
    public static boolean isNull(Object object) {
        return (object == null);
    }

    /**
     * 判断对象是否不为<code>null</code>
     * 
     * @param object 传入对象
     * @return 不为<code>null</code>则返还<code>true</code>
     */
    public static boolean isNotNull(Object object) {
        return (object != null);
    }

    /**
     * 判断对象是否均为<code>null</code>
     * 
     * @param objects 传入对象集
     * @return 均为<code>null</code>则返还<code>true</code>
     */
    public static boolean isAllNull(Object...objects) {
        if (objects == null) {
            return true;
        }

        for (Object object : objects) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有任一对象为<code>null</code>
     * 
     * @param objects 传入对象集
     * @return 任一对象为<code>null</code>则返还<code>true</code>
     */
    public static boolean isAnyNull(Object...objects) {
        if (objects == null) {
            return true;
        }

        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static int nullNum(Object...objects) {
        if (objects == null) {
            return -1;
        }

        int result = 0;
        for (Object object : objects) {
            if (object == null) {
                result++;
            }
        }
        return result;
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出——追加到
     * <code>StringBuilder</code>中。
     * 
     * <pre>
     * ObjectUtil.appendIdentityToString(*, null)            = null
     * ObjectUtil.appendIdentityToString(null, &quot;&quot;)           = &quot;java.lang.String@1e23&quot;
     * ObjectUtil.appendIdentityToString(null, Boolean.TRUE) = &quot;java.lang.Boolean@7fa&quot;
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append(&quot;java.lang.Boolean@7fa&quot;)
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append(&quot;int[]@7fa&quot;)
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append(&quot;java.lang.Object[]@7fa&quot;)
     * </pre>
     * 
     * @param builder <code>StringBuilder</code>对象，如果是<code>null</code>，则创建新的
     * @param object 对象
     * 
     * @return <code>StringBuilder</code>对象，如果对象为<code>null</code>，则返回 <code>null</code>
     */
    public static StringBuilder appendIdentityToString(StringBuilder builder, Object object) {
        if (object == null) {
            return null;
        }

        if (builder == null) {
            builder = new StringBuilder();
        }
        // FIXME
        builder.append(ClassUtil.getSimpleClassNameForObject(object));

        return builder.append('@').append(Integer.toHexString(identityHashCode(object)));
    }

    /**
     * 将对象自身的identity——如同对象没有覆盖<code>toString()</code>方法时， <code>Object.toString()</code>的原始输出——追加到
     * <code>StringBuffer</code>中。
     * 
     * <pre>
     * ObjectUtil.appendIdentityToString(*, null)            = null
     * ObjectUtil.appendIdentityToString(null, &quot;&quot;)           = &quot;java.lang.String@1e23&quot;
     * ObjectUtil.appendIdentityToString(null, Boolean.TRUE) = &quot;java.lang.Boolean@7fa&quot;
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append(&quot;java.lang.Boolean@7fa&quot;)
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append(&quot;int[]@7fa&quot;)
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append(&quot;java.lang.Object[]@7fa&quot;)
     * </pre>
     * 
     * @param buffer <code>StringBuffer</code>对象，如果是<code>null</code>，则创建新的
     * @param object 对象
     * 
     * @return <code>StringBuffer</code>对象，如果对象为<code>null</code>，则返回 <code>null</code>
     */
    public static StringBuffer appendIdentityToString(StringBuffer buffer, Object object) {
        if (object == null) {
            return null;
        }

        if (buffer == null) {
            buffer = new StringBuffer();
        }
        // FIXME
        buffer.append(ClassUtil.getSimpleClassNameForObject(object));

        return buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
    }

    /*
     * ========================================================================== ==
     */
    /* Clone函数。 */
    /*                                                                              */
    /* 以下方法调用Object.clone方法，默认是“浅复制”（shallow copy）。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 复制一个对象。如果对象为<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 此方法调用<code>Object.clone</code>方法，默认只进行“浅复制”。 对于数组，调用 <code>ArrayUtil.clone</code>方法更高效。
     * </p>
     * 
     * @param array 要复制的数组
     * 
     * @return 数组的复本，如果原始数组为<code>null</code>，则返回<code>null</code>
     * @throws CloneNotSupportedException
     */
    public static Object clone(Object array) throws CloneNotSupportedException {
        if (array == null) {
            return null;
        }

        // 对数组特殊处理
        if (Object[].class.isInstance(array)) {
            return ArrayUtil.clone((Object[]) array);
        }

        if (long[].class.isInstance(array)) {
            return ArrayUtil.clone((long[]) array);
        }

        if (int[].class.isInstance(array)) {
            return ArrayUtil.clone((int[]) array);
        }

        if (short[].class.isInstance(array)) {
            return ArrayUtil.clone((short[]) array);
        }

        if (byte[].class.isInstance(array)) {
            return ArrayUtil.clone((byte[]) array);
        }

        if (double[].class.isInstance(array)) {
            return ArrayUtil.clone((double[]) array);
        }

        if (float[].class.isInstance(array)) {
            return ArrayUtil.clone((float[]) array);
        }

        if (boolean[].class.isInstance(array)) {
            return ArrayUtil.clone((boolean[]) array);
        }

        if (char[].class.isInstance(array)) {
            return ArrayUtil.clone((char[]) array);
        }

        // Not cloneable
        if (!(Cloneable.class.isInstance(array))) {
            throw new CloneNotSupportedException("Object of class " + array.getClass().getName() + " is not Cloneable");
        }

        // 用reflection调用clone方法
        Class<? extends Object> clazz = array.getClass();

        try {
            Method cloneMethod = clazz.getMethod("clone", Emptys.EMPTY_CLASS_ARRAY);

            return cloneMethod.invoke(array, Emptys.EMPTY_OBJECT_ARRAY);
        } catch (NoSuchMethodException e) {
            throw new CloneNotSupportedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new CloneNotSupportedException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new CloneNotSupportedException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new CloneNotSupportedException(e.getMessage());
        }
    }

}
