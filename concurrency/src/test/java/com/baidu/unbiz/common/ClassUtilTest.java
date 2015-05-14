/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;
import com.baidu.unbiz.common.test.TestUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月16日 下午5:05:29
 */
public class ClassUtilTest extends CachedLogger {

    @Test
    public void getClassNameAsResource() {
        assertNull(ClassUtil.getClassNameAsResource(""));
        assertNull(ClassUtil.getClassNameAsResource(null));

        assertEquals("java/lang/String.class", ClassUtil.getClassNameAsResource("java.lang.String"));

        assertEquals("xxx/yyy/zzz.class", ClassUtil.getClassNameAsResource("xxx.yyy.zzz"));
    }

    @Test
    public void isInstance() {
        assertFalse(ClassUtil.isInstance(null, null));
        assertFalse(ClassUtil.isInstance(Object.class, null));
        assertFalse(ClassUtil.isInstance(null, 0));

        assertTrue(ClassUtil.isInstance(Object.class, new Object()));
        assertTrue(ClassUtil.isInstance(Object.class, 0));
        assertTrue(ClassUtil.isInstance(Object.class, "test"));
        assertFalse(ClassUtil.isInstance(String.class, 0));
        assertTrue(ClassUtil.isInstance(String.class, "test"));
        assertFalse(ClassUtil.isInstance(Integer.class, "test"));
        assertTrue(ClassUtil.isInstance(Serializable.class, "test"));
        assertFalse(ClassUtil.isInstance(Serializable.class, new ClassUtilTest()));
        assertTrue(ClassUtil.isInstance(Integer.class, 0));
        assertTrue(ClassUtil.isInstance(int.class, 0));
        assertFalse(ClassUtil.isInstance(long.class, 0));
        assertFalse(ClassUtil.isInstance(Long.class, 0));

        assertFalse(ClassUtil.isInstance(int[].class, new Integer[] { 0 }));
        assertFalse(ClassUtil.isInstance(Integer[].class, new int[] { 0 }));
        assertTrue(ClassUtil.isInstance(int[].class, new int[] { 0 }));
        assertTrue(ClassUtil.isInstance(Integer[].class, new Integer[] { 0 }));
    }

    @Test
    public void canInstatnce() {
        assertFalse(ClassUtil.canInstatnce(null));

        assertFalse(ClassUtil.canInstatnce(void.class));
        assertTrue(ClassUtil.canInstatnce(double.class));

        assertTrue(ClassUtil.canInstatnce(int[].class));
        assertTrue(ClassUtil.canInstatnce(Void[].class));
        assertTrue(ClassUtil.canInstatnce(Test[][].class));
        assertTrue(ClassUtil.canInstatnce(String[].class));

        assertFalse(ClassUtil.canInstatnce(Void.class));
        assertTrue(ClassUtil.canInstatnce(Short.class));
        assertFalse(ClassUtil.canInstatnce(Test.class));
        assertFalse(ClassUtil.canInstatnce(Collections.class));
        assertFalse(ClassUtil.canInstatnce(Serializable.class));
        assertFalse(ClassUtil.canInstatnce(TimeUnit.class));

        assertTrue(ClassUtil.canInstatnce(InnerStatic.class));
        assertTrue(ClassUtil.canInstatnce(InnerStatic[].class));
        assertTrue(ClassUtil.canInstatnce(String.class));
        assertFalse(ClassUtil.canInstatnce(Inner.class));
        assertFalse(ClassUtil.canInstatnce(Inner[].class));
    }

    @Test
    public void getArrayClass() {
        // dim < 0
        try {
            ClassUtil.getArrayClass(int.class, -1);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e, TestUtil.exception("wrong dimension: -1"));
        }

        // null
        assertNull(ClassUtil.getArrayClass(null, 1));

        // form 1
        assertEquals(int[].class, ClassUtil.getArrayClass(int.class));
        assertEquals(int[][].class, ClassUtil.getArrayClass(int[].class));

        assertEquals(Integer[].class, ClassUtil.getArrayClass(Integer.class));
        assertEquals(Integer[][].class, ClassUtil.getArrayClass(Integer[].class));

        // form 2
        assertEquals(int.class, ClassUtil.getArrayClass(int.class, 0));
        assertEquals(int[].class, ClassUtil.getArrayClass(int.class, 1));
        assertEquals(int[][].class, ClassUtil.getArrayClass(int.class, 2));

        assertEquals(int[].class, ClassUtil.getArrayClass(int[].class, 0));
        assertEquals(int[][].class, ClassUtil.getArrayClass(int[].class, 1));
        assertEquals(int[][][].class, ClassUtil.getArrayClass(int[].class, 2));

        assertEquals(String.class, ClassUtil.getArrayClass(String.class, 0));
        assertEquals(String[].class, ClassUtil.getArrayClass(String.class, 1));
        assertEquals(String[][].class, ClassUtil.getArrayClass(String.class, 2));

        assertEquals(ClassUtilTest[].class, ClassUtil.getArrayClass(ClassUtilTest[].class, 0));
        assertEquals(ClassUtilTest[][].class, ClassUtil.getArrayClass(ClassUtilTest[].class, 1));
        assertEquals(ClassUtilTest[][][].class, ClassUtil.getArrayClass(ClassUtilTest[].class, 2));
    }

    @Test
    public void getArrayComponentType() {
        // not array
        assertNull(ClassUtil.getArrayComponentType(null));
        assertNull(ClassUtil.getArrayComponentType(int.class));
        assertNull(ClassUtil.getArrayComponentType(Object.class));

        assertEquals(ClassUtilTest.class, ClassUtil.getArrayComponentType(ClassUtilTest[].class));
        assertEquals(ClassUtilTest.class, ClassUtil.getArrayComponentType(ClassUtilTest[][].class));
        assertEquals(ClassUtilTest.class, ClassUtil.getArrayComponentType(ClassUtilTest[][][].class));

        assertEquals(int.class, ClassUtil.getArrayComponentType(int[].class));
        assertEquals(int.class, ClassUtil.getArrayComponentType(int[][].class));
        assertEquals(int.class, ClassUtil.getArrayComponentType(int[][][].class));

        assertEquals(Integer.class, ClassUtil.getArrayComponentType(Integer[].class));
        assertEquals(Integer.class, ClassUtil.getArrayComponentType(Integer[][].class));
        assertEquals(Integer.class, ClassUtil.getArrayComponentType(Integer[][][].class));

        assertNotEquals(Integer.class, ClassUtil.getArrayComponentType(int[].class));
        assertNotEquals(Integer.class, ClassUtil.getArrayComponentType(int[][].class));
        assertNotEquals(Integer.class, ClassUtil.getArrayComponentType(int[][][].class));
    }

    @Test
    public void getPrimitiveType() {
        assertEquals(int.class, ClassUtil.getPrimitiveType("int"));
        assertEquals(long.class, ClassUtil.getPrimitiveType("long"));
        assertEquals(short.class, ClassUtil.getPrimitiveType("short"));
        assertEquals(double.class, ClassUtil.getPrimitiveType("double"));
        assertEquals(float.class, ClassUtil.getPrimitiveType("float"));
        assertEquals(char.class, ClassUtil.getPrimitiveType("char"));
        assertEquals(byte.class, ClassUtil.getPrimitiveType("byte"));
        assertEquals(boolean.class, ClassUtil.getPrimitiveType("boolean"));
        assertEquals(void.class, ClassUtil.getPrimitiveType("void"));

        assertEquals(int.class, ClassUtil.getPrimitiveType("java.lang.Integer"));
        assertEquals(long.class, ClassUtil.getPrimitiveType("java.lang.Long"));
        assertEquals(short.class, ClassUtil.getPrimitiveType("java.lang.Short"));
        assertEquals(double.class, ClassUtil.getPrimitiveType("java.lang.Double"));
        assertEquals(float.class, ClassUtil.getPrimitiveType("java.lang.Float"));
        assertEquals(char.class, ClassUtil.getPrimitiveType("java.lang.Character"));
        assertEquals(byte.class, ClassUtil.getPrimitiveType("java.lang.Byte"));
        assertEquals(boolean.class, ClassUtil.getPrimitiveType("java.lang.Boolean"));
        assertEquals(void.class, ClassUtil.getPrimitiveType("java.lang.Void"));

        assertEquals(null, ClassUtil.getPrimitiveType("java.lang.String"));

        assertEquals(int.class, ClassUtil.getPrimitiveType(Integer.class));
        assertEquals(long.class, ClassUtil.getPrimitiveType(Long.class));
        assertEquals(short.class, ClassUtil.getPrimitiveType(Short.class));
        assertEquals(double.class, ClassUtil.getPrimitiveType(Double.class));
        assertEquals(float.class, ClassUtil.getPrimitiveType(Float.class));
        assertEquals(char.class, ClassUtil.getPrimitiveType(Character.class));
        assertEquals(byte.class, ClassUtil.getPrimitiveType(Byte.class));
        assertEquals(boolean.class, ClassUtil.getPrimitiveType(Boolean.class));
        assertEquals(void.class, ClassUtil.getPrimitiveType(Void.class));

        assertEquals(null, ClassUtil.getPrimitiveType(String.class));
    }

    @Test
    public void getWrapperTypeIfPrimitive() {
        assertEquals(Integer.class, ClassUtil.getWrapperTypeIfPrimitive(int.class));
        assertEquals(Long.class, ClassUtil.getWrapperTypeIfPrimitive(long.class));
        assertEquals(Short.class, ClassUtil.getWrapperTypeIfPrimitive(short.class));
        assertEquals(Double.class, ClassUtil.getWrapperTypeIfPrimitive(double.class));
        assertEquals(Float.class, ClassUtil.getWrapperTypeIfPrimitive(float.class));
        assertEquals(Character.class, ClassUtil.getWrapperTypeIfPrimitive(char.class));
        assertEquals(Byte.class, ClassUtil.getWrapperTypeIfPrimitive(byte.class));
        assertEquals(Boolean.class, ClassUtil.getWrapperTypeIfPrimitive(boolean.class));
        assertEquals(Void.class, ClassUtil.getWrapperTypeIfPrimitive(void.class));

        assertEquals(int[][].class, ClassUtil.getWrapperTypeIfPrimitive(int[][].class));
        assertEquals(long[][].class, ClassUtil.getWrapperTypeIfPrimitive(long[][].class));
        assertEquals(short[][].class, ClassUtil.getWrapperTypeIfPrimitive(short[][].class));
        assertEquals(double[][].class, ClassUtil.getWrapperTypeIfPrimitive(double[][].class));
        assertEquals(float[][].class, ClassUtil.getWrapperTypeIfPrimitive(float[][].class));
        assertEquals(char[][].class, ClassUtil.getWrapperTypeIfPrimitive(char[][].class));
        assertEquals(byte[][].class, ClassUtil.getWrapperTypeIfPrimitive(byte[][].class));
        assertEquals(boolean[][].class, ClassUtil.getWrapperTypeIfPrimitive(boolean[][].class));

        assertEquals(String.class, ClassUtil.getWrapperTypeIfPrimitive(String.class));

        assertEquals(String[][].class, ClassUtil.getWrapperTypeIfPrimitive(String[][].class));
    }

    @Test
    public void getPrimitiveDefaultValue() {
        assertEquals(new Integer(0), ClassUtil.getPrimitiveDefaultValue(int.class));
        assertEquals(new Long(0), ClassUtil.getPrimitiveDefaultValue(long.class));
        assertEquals(new Short((short) 0), ClassUtil.getPrimitiveDefaultValue(short.class));
        assertEquals(new Double(0), ClassUtil.getPrimitiveDefaultValue(double.class));
        assertEquals(new Float(0), ClassUtil.getPrimitiveDefaultValue(float.class));
        assertEquals(new Character('\0'), ClassUtil.getPrimitiveDefaultValue(char.class));
        assertEquals(new Byte((byte) 0), ClassUtil.getPrimitiveDefaultValue(byte.class));
        assertEquals(Boolean.FALSE, ClassUtil.getPrimitiveDefaultValue(boolean.class));
        assertEquals(null, ClassUtil.getPrimitiveDefaultValue(void.class));

        assertEquals(new Integer(0), ClassUtil.getPrimitiveDefaultValue(Integer.class));
        assertEquals(new Long(0), ClassUtil.getPrimitiveDefaultValue(Long.class));
        assertEquals(new Short((short) 0), ClassUtil.getPrimitiveDefaultValue(Short.class));
        assertEquals(new Double(0), ClassUtil.getPrimitiveDefaultValue(Double.class));
        assertEquals(new Float(0), ClassUtil.getPrimitiveDefaultValue(Float.class));
        assertEquals(new Character('\0'), ClassUtil.getPrimitiveDefaultValue(Character.class));
        assertEquals(new Byte((byte) 0), ClassUtil.getPrimitiveDefaultValue(Byte.class));
        assertEquals(Boolean.FALSE, ClassUtil.getPrimitiveDefaultValue(Boolean.class));
        assertEquals(null, ClassUtil.getPrimitiveDefaultValue(Void.class));

        assertEquals(null, ClassUtil.getPrimitiveDefaultValue(String.class));
        assertEquals(null, (Object) ClassUtil.getPrimitiveDefaultValue(long[][].class));
    }

    @Test
    public void isPrimitiveOrWrapper() {
        assertTrue(ClassUtil.isPrimitiveOrWrapper(int.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(long.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(short.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(double.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(float.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(char.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(byte.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(boolean.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(void.class));

        assertTrue(ClassUtil.isPrimitiveOrWrapper(Integer.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Long.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Short.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Double.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Float.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Character.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Byte.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Boolean.class));
        assertTrue(ClassUtil.isPrimitiveOrWrapper(Void.class));

        assertFalse(ClassUtil.isPrimitiveOrWrapper(int[].class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(long[].class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Double[].class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Boolean[].class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(String.class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Object.class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Enum.class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Override.class));
        assertFalse(ClassUtil.isPrimitiveOrWrapper(Compiler.class));
    }

    @Test
    public void getAllSuperclasses() {
        assertEquals(null, ClassUtil.getAllSuperclasses(null));
        assertTrue(ClassUtil.getAllSuperclasses(Object.class).isEmpty());

        assertTrue(ClassUtil.getAllSuperclasses(String.class).isEmpty());

        List<Class<?>> supers = ClassUtil.getAllSuperclasses(ArrayList.class);
        assertTrue(supers.contains(AbstractList.class));
        assertTrue(supers.contains(AbstractCollection.class));

        assertFalse(supers.contains(List.class));
        assertFalse(supers.contains(Object.class));

        assertTrue(ClassUtil.getAllSuperclasses(int.class).isEmpty());
        assertTrue(ClassUtil.getAllSuperclasses(int[].class).isEmpty());
        assertTrue(ClassUtil.getAllSuperclasses(Integer.class).contains(Number.class));

        assertTrue(ClassUtil.getAllSuperclasses(Integer[].class).isEmpty());

    }

    @Test
    public void getAllInterfaces() {
        assertEquals(null, ClassUtil.getAllInterfaces(null));
        assertTrue(ClassUtil.getAllInterfaces(Object.class).isEmpty());

        assertFalse(ClassUtil.getAllInterfaces(String.class).isEmpty());
        assertFalse(ClassUtil.getAllInterfaces(Class.class).isEmpty());

        List<Class<?>> supers = ClassUtil.getAllInterfaces(ArrayList.class);
        assertFalse(supers.contains(AbstractList.class));
        assertFalse(supers.contains(AbstractCollection.class));

        assertTrue(supers.contains(List.class));
        assertTrue(supers.contains(RandomAccess.class));
        assertTrue(supers.contains(Iterable.class));
        assertFalse(supers.contains(Object.class));

        assertTrue(ClassUtil.getAllInterfaces(int.class).isEmpty());
        // assertTrue(ClassUtil.getAllInterfaces(int[].class).isEmpty());
        assertFalse(ClassUtil.getAllInterfaces(Integer.class).contains(Number.class));
        assertTrue(ClassUtil.getAllInterfaces(Integer.class).contains(Comparable.class));

        List<Class<?>> list = ClassUtil.getAllInterfaces(Integer[].class);
        assertTrue(list.contains(Cloneable.class));
        assertTrue(list.contains(Serializable.class));

    }

    public static class InnerStatic {

    }

    class Inner {

    }

}
