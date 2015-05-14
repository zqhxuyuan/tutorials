/**
 * 
 */
package com.baidu.unbiz.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.loading.MLet;

import org.junit.Test;

import com.baidu.unbiz.common.logger.CachedLogger;
import com.baidu.unbiz.common.sample.AnnotationClass;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月17日 下午4:46:10
 */
public class ReflectionUtilTest extends CachedLogger {

    @Test
    public void getAllMethodsOfClass() {
        assertNull(ReflectionUtil.getAllMethodsOfClass(null));

        Method[] methods = ReflectionUtil.getAllMethodsOfClass(MLet.class);
        assertTrue(methods.length > 0);

        Method equalsMethod = ReflectionUtil.getMethod(Object.class, "equals", Object.class);

        assertTrue(methods.length > 0);
        List<Method> methodList = Arrays.asList(methods);

        assertFalse(methodList.contains(equalsMethod));

        List<Class<?>> list = ClassUtil.getAllInterfaces(MLet.class);

        int interMethodLength = 0;
        for (Class<?> clazz : list) {
            Method[] interMethods = ReflectionUtil.getAllMethodsOfClass(clazz);
            interMethodLength += interMethods.length;
        }

        assertTrue(methods.length > interMethodLength);
    }

    @Test
    public void findAllMethodAnnotation() {
        assertNull(ReflectionUtil.findAllMethodAnnotation((Class<?>) null, (Class<? extends Annotation>) null));

        assertNull(ReflectionUtil.findAllMethodAnnotation((Class<?>) null, AnnotationClass.Test.class));
        assertNull(ReflectionUtil.findAllMethodAnnotation(AnnotationClass.class, (Class<? extends Annotation>) null));

        List<AnnotationClass.Test> list =
                ReflectionUtil.findAllMethodAnnotation(AnnotationClass.class, AnnotationClass.Test.class);

        assertTrue(list.size() == 8);

        List<Test> newList = ReflectionUtil.findAllMethodAnnotation(AnnotationClass.class, Test.class);

        assertTrue(CollectionUtil.isEmpty(newList));
    }

    @Test
    public void getAnnotationMethods() {
        assertNull(ReflectionUtil.getAnnotationMethods((Class<?>) null, (Class<? extends Annotation>) null));

        assertNull(ReflectionUtil.getAnnotationMethods((Class<?>) null, AnnotationClass.Test.class));
        assertNull(ReflectionUtil.getAnnotationMethods(AnnotationClass.class, (Class<? extends Annotation>) null));

        List<Method> list = ReflectionUtil.getAnnotationMethods(AnnotationClass.class, AnnotationClass.Test.class);

        assertTrue(list.size() == 8);

        list = ReflectionUtil.getAnnotationMethods(AnnotationClass.class, Test.class);

        assertTrue(CollectionUtil.isEmpty(list));
    }

    @Test
    public void invokeMethod() {
        assertNull(ReflectionUtil.invokeMethod(null, null));
        assertNull(ReflectionUtil.invokeMethod(null, new Object(), new Object()));
        assertNull(ReflectionUtil.invokeMethod(null, new Object()));

        assertNull(ReflectionUtil.invokeMethod((Object) null, (String) null, null));
        assertNull(ReflectionUtil.invokeMethod((Object) null, (String) null, (Object[]) null, (Class<?>) null));
        assertNull(ReflectionUtil.invokeMethod("", (String) null, (Object[]) null, (Class<?>) null));
        assertNull(ReflectionUtil.invokeMethod((Object) null, (String) null, new Object[] {}, (Class<?>) null));
        assertNull(ReflectionUtil.invokeMethod((Object) null, (String) null, (Object[]) null, Emptys.EMPTY_CLASS_ARRAY));

        assertNull(ReflectionUtil.invokeMethod(null, new Object(), new Object()));
        assertNull(ReflectionUtil.invokeMethod(null, new Object()));

        Method method = null;
        try {
            method = String.class.getMethod("valueOf", int.class);
            assertEquals("1", ReflectionUtil.invokeMethod(method, null, 1));
            assertEquals("1", ReflectionUtil.invokeMethod(method, "", 1));
            assertEquals("1", ReflectionUtil.invokeMethod(method, new Object(), 1));

            method = String.class.getMethod("trim");
            assertEquals("xxx", ReflectionUtil.invokeMethod(method, " xxx "));
            assertEquals("xxx", ReflectionUtil.invokeMethod(method, new Object()));

        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }

        List<String> list = CollectionUtil.createArrayList();

        try {
            method = ArrayList.class.getDeclaredMethod("RangeCheck", int.class);
            ReflectionUtil.invokeMethod(method, list, Integer.MAX_VALUE);
        } catch (Exception e) {
            InvocationTargetException ex = (InvocationTargetException) e.getCause();

            if (ex != null) {
                assertTrue(ex.getTargetException() instanceof IndexOutOfBoundsException);
            }
        }

        try {

            assertEquals("xxx", ReflectionUtil.invokeMethod(" xxx ", "trim", null));
            assertEquals("xxx", ReflectionUtil.invokeMethod(new Object(), "trim", null));

        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }

        list = CollectionUtil.createArrayList();

        try {
            ReflectionUtil.invokeMethod(list, "RangeCheck", new Object[] { Integer.MAX_VALUE }, int.class);
        } catch (Exception e) {

            if (e.getCause() instanceof NoSuchMethodException) {

            } else {

                InvocationTargetException ex = (InvocationTargetException) e.getCause();

                assertTrue(ex.getTargetException() instanceof IndexOutOfBoundsException);
            }
        }

    }

    @Test
    public void getAllFieldsOfClass() {
        assertNull(ReflectionUtil.getAllFieldsOfClass(null));
        assertNull(ReflectionUtil.getAllFieldsOfClass(Object.class));

        assertEquals(0, ReflectionUtil.getAllFieldsOfClass(List.class).length);
        Field[] fields = ReflectionUtil.getAllFieldsOfClass(String.class);
        assertTrue(fields.length > 0);

        Field[] instancefields = ReflectionUtil.getAllInstanceFields(String.class);
        assertTrue(instancefields.length > 0);

        assertTrue(fields.length - instancefields.length > 0);

    }

    @Test
    public void getAnnotationFields() {
        assertNull(ReflectionUtil.getAnnotationFields((Class<?>) null, (Class<? extends Annotation>) null));

        assertNull(ReflectionUtil.getAnnotationFields((Class<?>) null, AnnotationClass.Test.class));
        assertNull(ReflectionUtil.getAnnotationFields(AnnotationClass.class, (Class<? extends Annotation>) null));

        Field[] fields = ReflectionUtil.getAnnotationFields(AnnotationClass.class, AnnotationClass.Test.class);

        assertTrue(fields.length == 2);

        fields = ReflectionUtil.getAnnotationFields(AnnotationClass.class, Test.class);

        assertTrue(ObjectUtil.isEmpty(fields));

    }

    @Test
    public void getComponentType() throws Exception {
        Field f1 = BaseClass.class.getField("f1");
        Field f5 = ConcreteClass.class.getField("f5");

        assertNull(ReflectionUtil.getComponentType(f1.getGenericType()));
        assertEquals(Long.class, ReflectionUtil.getComponentType(f5.getGenericType()));
    }

    @Test
    public void getGenericSuperType() throws Exception {
        Class<?>[] genericSupertypes = ReflectionUtil.getGenericSuperTypes(ConcreteClass.class);
        assertEquals(String.class, genericSupertypes[0]);
        assertEquals(Integer.class, genericSupertypes[1]);
    }

    @Test
    public void getRowType() throws Exception {
        Field f1 = BaseClass.class.getField("f1");
        Field f2 = BaseClass.class.getField("f2");
        Field f3 = BaseClass.class.getField("f3");
        Field f4 = ConcreteClass.class.getField("f4");
        Field f5 = ConcreteClass.class.getField("f5");
        Field array1 = BaseClass.class.getField("array1");

        assertEquals(String.class, ReflectionUtil.getRawType(f1.getGenericType(), ConcreteClass.class));
        assertEquals(Integer.class, ReflectionUtil.getRawType(f2.getGenericType(), ConcreteClass.class));
        assertEquals(String.class, ReflectionUtil.getRawType(f3.getGenericType(), ConcreteClass.class));
        assertEquals(Long.class, ReflectionUtil.getRawType(f4.getGenericType(), ConcreteClass.class));
        assertEquals(List.class, ReflectionUtil.getRawType(f5.getGenericType(), ConcreteClass.class));
        assertEquals(String[].class, ReflectionUtil.getRawType(array1.getGenericType(), ConcreteClass.class));

        assertEquals(Object.class, ReflectionUtil.getRawType(f1.getGenericType()));
    }

    public static class BaseClass<A, B> {
        public A f1;
        public B f2;
        public String f3;
        public A[] array1;
    }

    public static class ConcreteClass extends BaseClass<String, Integer> {
        public Long f4;
        public List<Long> f5;
    }

    public static class BaseClass2<X> extends BaseClass<X, Integer> {
    }

    public static class ConcreteClass2 extends BaseClass2<String> {
    }

    public static class Soo {
        public List<String> stringList;
        public String[] strings;
        public String string;

        public List<Integer> getIntegerList() {
            return null;
        }

        public Integer[] getIntegers() {
            return null;
        }

        public Integer getInteger() {
            return null;
        }

        public <T> T getTemplate(T foo) {
            return null;
        }

        public Collection<? extends Number> getCollection() {
            return null;
        }

        public Collection<?> getCollection2() {
            return null;
        }
    }

    @Test
    public void testGetRawAndComponentType() throws NoSuchFieldException {

        Class<Soo> sooClass = Soo.class;

        Field stringList = sooClass.getField("stringList");
        assertEquals(List.class, ReflectionUtil.getRawType(stringList.getType()));
        assertEquals(String.class, ReflectionUtil.getComponentType(stringList.getGenericType()));

        Field strings = sooClass.getField("strings");
        assertEquals(String[].class, ReflectionUtil.getRawType(strings.getType()));
        assertEquals(String.class, ReflectionUtil.getComponentType(strings.getGenericType()));

        Field string = sooClass.getField("string");
        assertEquals(String.class, ReflectionUtil.getRawType(string.getType()));
        assertNull(ReflectionUtil.getComponentType(string.getGenericType()));

        Method integerList = ReflectionUtil.getMethod(sooClass, "getIntegerList");
        assertEquals(List.class, ReflectionUtil.getRawType(integerList.getReturnType()));
        assertEquals(Integer.class, ReflectionUtil.getComponentType(integerList.getGenericReturnType()));

        Method integers = ReflectionUtil.getMethod(sooClass, "getIntegers");
        assertEquals(Integer[].class, ReflectionUtil.getRawType(integers.getReturnType()));
        assertEquals(Integer.class, ReflectionUtil.getComponentType(integers.getGenericReturnType()));

        Method integer = ReflectionUtil.getMethod(sooClass, "getInteger");
        assertEquals(Integer.class, ReflectionUtil.getRawType(integer.getReturnType()));
        assertNull(ReflectionUtil.getComponentType(integer.getGenericReturnType()));

        // Method template = ReflectionUtil.getMethod(sooClass, "getTemplate");
        // assertEquals(Object.class, ReflectionUtil.getRawType(template.getReturnType()));
        // assertNull(ReflectionUtil.getComponentType(template.getGenericReturnType()));

        Method collection = ReflectionUtil.getMethod(sooClass, "getCollection");
        assertEquals(Collection.class, ReflectionUtil.getRawType(collection.getReturnType()));
        assertEquals(Number.class, ReflectionUtil.getComponentType(collection.getGenericReturnType()));

        Method collection2 = ReflectionUtil.getMethod(sooClass, "getCollection2");
        assertEquals(Collection.class, ReflectionUtil.getRawType(collection2.getReturnType()));
        assertEquals(Object.class, ReflectionUtil.getComponentType(collection2.getGenericReturnType()));
    }

    public static class Base2<N extends Number, K> {
        public N getNumber() {
            return null;
        }

        public K getKiko() {
            return null;
        }
    }

    public static class Impl1<N extends Number> extends Base2<N, Long> {
    }

    public static class Impl2 extends Impl1<Integer> {
    }

    @Test
    public void testGetRawWithImplClass() throws NoSuchFieldException {
        Method number = ReflectionUtil.getMethod(Base2.class, "getNumber");
        Method kiko = ReflectionUtil.getMethod(Base2.class, "getKiko");

        assertEquals(Number.class, ReflectionUtil.getRawType(number.getReturnType()));
        assertEquals(Number.class, ReflectionUtil.getRawType(number.getGenericReturnType()));

        assertEquals(Object.class, ReflectionUtil.getRawType(kiko.getReturnType()));
        assertEquals(Object.class, ReflectionUtil.getRawType(kiko.getGenericReturnType()));

        assertEquals(Number.class, ReflectionUtil.getRawType(number.getReturnType(), Impl1.class));
        assertEquals(Number.class, ReflectionUtil.getRawType(number.getGenericReturnType(), Impl1.class));

        assertEquals(Object.class, ReflectionUtil.getRawType(kiko.getReturnType(), Impl1.class));
        assertEquals(Long.class, ReflectionUtil.getRawType(kiko.getGenericReturnType(), Impl1.class));

        assertEquals(Number.class, ReflectionUtil.getRawType(number.getReturnType(), Impl2.class));
        assertEquals(Integer.class, ReflectionUtil.getRawType(number.getGenericReturnType(), Impl2.class));

        assertEquals(Object.class, ReflectionUtil.getRawType(kiko.getReturnType(), Impl2.class));
        assertEquals(Long.class, ReflectionUtil.getRawType(kiko.getGenericReturnType(), Impl2.class));
    }

    public static class FieldType<K extends Number, V extends List<String> & Collection<String>> {
        List<?> fRaw;
        List<Object> fTypeObject;
        List<String> fTypeString;
        List<?> fWildcard;
        List<? super List<String>> fBoundedWildcard;
        Map<String, List<Set<Long>>> fTypeNested;
        Map<K, V> fTypeLiteral;
        K[] fGenericArray;
    }

    public static class MethodReturnType {
        List<?> mRaw() {
            return null;
        }

        List<String> mTypeString() {
            return null;
        }

        List<?> mWildcard() {
            return null;
        }

        List<? extends Number> mBoundedWildcard() {
            return null;
        }

        <T extends List<String>> List<T> mTypeLiteral() {
            return null;
        }
    }

    public static class MethodParameterType<A> {
        <T extends List<T>> void m(A a, String p1, T p2, List<?> p3, List<T> p4) {
        }
    }

    public static class Mimple extends MethodParameterType<Long> {
    }

    @Test
    public void testMethodParameterTypeToString() {
        Method method = null;
        for (Method m : MethodParameterType.class.getDeclaredMethods()) {

            method = m;
        }

        Type[] types = method.getGenericParameterTypes();
        assertEquals(Object.class, ReflectionUtil.getRawType(types[0], MethodParameterType.class));
        assertEquals(String.class, ReflectionUtil.getRawType(types[1], MethodParameterType.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[2], MethodParameterType.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[3], MethodParameterType.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[4], MethodParameterType.class));

        // same methods, using different impl class
        assertEquals(Long.class, ReflectionUtil.getRawType(types[0], Mimple.class)); // change!
        assertEquals(String.class, ReflectionUtil.getRawType(types[1], Mimple.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[2], Mimple.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[3], Mimple.class));
        assertEquals(List.class, ReflectionUtil.getRawType(types[4], Mimple.class));
    }

    public interface SomeGuy {
    }

    public interface Cool extends SomeGuy {
    }

    public interface Vigilante {
    }

    public interface Flying extends Vigilante {
    }

    public interface SuperMario extends Flying, Cool {
    };

    public class User implements SomeGuy {
    }

    public class SuperUser extends User implements Cool {
    }

    public class SuperMan extends SuperUser implements Flying {
    }

}
