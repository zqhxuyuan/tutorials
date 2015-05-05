/**
 * 
 */
package com.baidu.unbiz.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;

/**
 * 有关 <code>Reflection</code> 处理的工具类。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * </p>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午7:07:16
 */
public abstract class ReflectionUtil {

    // ==========================================================================
    // Method相关的方法。
    // ==========================================================================

    /**
     * 获取类的所有<code>Method</code>，不包括<code>java.lang.Object</code>的 <code>Method</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * 
     * @param clazz 要获取的类
     * @return <code>Method</code>数组
     */
    public static Method[] getAllMethodsOfClass(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Method[] methods = null;
        Class<?> itr = clazz;
        while (itr != null && !itr.equals(Object.class)) {
            methods = (Method[]) ArrayUtil.addAll(itr.getDeclaredMethods(), methods);
            itr = itr.getSuperclass();
        }
        return methods;
    }

    /**
     * 获取类的所有<code>Method</code>，不包括<code>java.lang.Object</code>的 <code>Method</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param clazz 要获取的类
     * @param accessible 是否允许访问
     * @return <code>Method</code>数组
     */
    public static Method[] getAllMethodsOfClass(Class<?> clazz, boolean accessible) {
        Method[] methods = getAllMethodsOfClass(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            AccessibleObject.setAccessible(methods, accessible);
        }

        return methods;
    }

    /**
     * 获取类的所有实例<code>Method</code>，不包括<code>java.lang.Object</code>的 <code>Method</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * 
     * @param clazz 要获取的类
     * @return <code>Method</code>数组
     */
    public static Method[] getAllInstanceMethods(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<Method> methods = CollectionUtil.createArrayList();
        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            for (Method method : itr.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    methods.add(method);
                }
            }
            itr = itr.getSuperclass();
        }

        return methods.toArray(new Method[methods.size()]);

    }

    /**
     * 获取类的所有实例<code>Method</code>，不包括<code>java.lang.Object</code>的 <code>Method</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param clazz 要获取的类
     * @param accessible 是否允许访问
     * @return <code>Method</code>数组
     */
    public static Method[] getAllInstanceMethods(Class<?> clazz, boolean accessible) {
        Method[] methods = getAllInstanceMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            AccessibleObject.setAccessible(methods, accessible);
        }

        return methods;
    }

    /**
     * 获取<code>clazz</code>下所有<code>Method</code>带指定<code>Annotation</code>的 <code>Annotation</code>集合，这句话说的不太好，还是看英文名清楚
     * <p>
     * 如果 <code>clazz</code>或<code>annotationType</code>为<code>null</code> ，则返还 <code>null</code>
     * 
     * @param clazz 要获取的类
     * @param annotationType 指定的<code>Annotation</code>
     * @return code>clazz</code>下所有<code>Method</code>带指定<code>Annotation</code> 的 <code>Annotation</code>集合
     */
    public static <T, A extends Annotation> List<A> findAllMethodAnnotation(Class<T> clazz, Class<A> annotationType) {
        if (clazz == null || annotationType == null) {
            return null;
        }

        List<A> annotations = CollectionUtil.createArrayList();
        Method[] methods = getAllMethodsOfClass(clazz);

        for (Method method : methods) {
            A annotation = method.getAnnotation(annotationType);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }

        return annotations;
    }

    /**
     * 获取<code>clazz</code>下所有带指定<code>Annotation</code>的<code>Method</code>集合。
     * <p>
     * 如果 <code>clazz</code>或<code>annotationType</code>为<code>null</code> ，则返还 <code>null</code>
     * 
     * @param clazz 要获取的类
     * @param annotationType 指定的<code>Annotation</code>
     * @return <code>clazz</code>下所有带指定<code>Annotation</code>的 <code>Method</code>集合
     */
    public static <T, A extends Annotation> List<Method> getAnnotationMethods(Class<T> clazz, Class<A> annotationType) {
        if (clazz == null || annotationType == null) {
            return null;
        }
        List<Method> list = CollectionUtil.createArrayList();

        for (Method method : getAllMethodsOfClass(clazz)) {
            A type = method.getAnnotation(annotationType);
            if (type != null) {
                list.add(method);
            }
        }

        return list;
    }

    /**
     * 方法调用，如果<code>clazz</code>为<code>null</code>，返回<code>null</code>；
     * <p>
     * 如果<code>method</code>为<code>null</code>，返回<code>null</code>
     * <p>
     * 如果<code>target</code>为<code>null</code>，则为静态方法
     * 
     * @param method 调用的方法
     * @param target 目标对象
     * @param args 方法的参数值
     * @return 调用结果
     */
    public static Object invokeMethod(Method method, Object target, Object...args) {
        if (method == null) {
            return null;
        }

        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }

    }

    /**
     * <p>
     * 调用一个命名的方法，其参数类型相匹配的对象类型。
     * </p>
     * 
     * 
     * @param object 调用方法作用的对象
     * @param methodName 方法名
     * @param args 参数值
     * @param parameterTypes 参数类型
     * @return 调用的方法的返回值
     * 
     */
    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>...parameterTypes) {
        if (object == null || StringUtil.isEmpty(methodName)) {
            return null;
        }

        if (parameterTypes == null) {
            parameterTypes = Emptys.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = Emptys.EMPTY_OBJECT_ARRAY;
        }
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }
        if (method == null) {
            return null;
        }

        return invokeMethod(method, object, args);

    }

    /**
     * <p>
     * 调用一个命名的静态方法，其参数类型相匹配的对象类型。
     * </p>
     * 
     * 
     * @param clazz 调用方法作用的类
     * @param methodName 方法名
     * @param args 参数值
     * @param parameterTypes 参数类型
     * @return 调用的方法的返回值
     * 
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object[] args, Class<?>...parameterTypes) {
        if (parameterTypes == null) {
            parameterTypes = Emptys.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = Emptys.EMPTY_OBJECT_ARRAY;
        }
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }
        if (method == null) {
            return null;
        }

        return invokeMethod(method, null, args);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>...parameterTypes) {
        if (clazz == null || StringUtil.isBlank(methodName)) {
            return null;
        }

        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            Method[] methods = itr.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                    return method;
                }
            }

            itr = itr.getSuperclass();
        }

        return null;

    }

    public static Method getMethod(Object object, String methodName, Class<?>...parameterTypes) {
        if (object == null || StringUtil.isBlank(methodName)) {
            return null;
        }

        for (Class<?> itr = object.getClass(); hasSuperClass(itr);) {
            Method[] methods = itr.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                    return method;
                }
            }

            itr = itr.getSuperclass();
        }

        return null;

    }

    public static Method[] getAccessibleMethods(Class<?> clazz) {
        return getAccessibleMethods(clazz, Object.class);
    }

    public static Method[] getAccessibleMethods(Class<?> clazz, Class<?> limit) {
        Package topPackage = clazz.getPackage();
        List<Method> methodList = CollectionUtil.createArrayList();
        int topPackageHash = (topPackage == null) ? 0 : topPackage.hashCode();
        boolean top = true;
        do {
            if (clazz == null) {
                break;
            }
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (Modifier.isVolatile(method.getModifiers())) {
                    continue;
                }
                if (top) {
                    methodList.add(method);
                    continue;
                }
                int modifier = method.getModifiers();
                if (Modifier.isPrivate(modifier) || Modifier.isAbstract(modifier)) {
                    continue;
                }

                if (Modifier.isPublic(modifier) || Modifier.isProtected(modifier)) {
                    addMethodIfNotExist(methodList, method);
                    continue;
                }
                // add super default methods from the same package
                Package pckg = method.getDeclaringClass().getPackage();
                int pckgHash = (pckg == null) ? 0 : pckg.hashCode();
                if (pckgHash == topPackageHash) {
                    addMethodIfNotExist(methodList, method);
                }
            }
            top = false;
        } while ((clazz = clazz.getSuperclass()) != limit);

        Method[] methods = new Method[methodList.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = methodList.get(i);
        }
        return methods;
    }

    private static void addMethodIfNotExist(List<Method> allMethods, Method newMethod) {
        for (Method method : allMethods) {
            if (ObjectUtil.isEquals(method, newMethod)) {
                return;
            }
        }

        allMethods.add(newMethod);
    }

    public static <A extends Annotation> boolean hasAnnotation(Method method, Class<A> annotationType) {
        if (ObjectUtil.isAnyNull(method, annotationType)) {
            return false;
        }

        return method.getAnnotation(annotationType) != null;
    }

    // ==========================================================================
    // Field相关的方法。
    // ==========================================================================

    /**
     * 获取类及父类的所有<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param clazz 要获取的类
     * @return <code>Field</code>数组
     */
    public static Field[] getAllFieldsOfClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return _getAllFieldsOfClass(clazz);
    }

    /**
     * 获取类及父类的所有非静态<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * <p>
     * 如果<code>clazz</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param clazz 要获取的类
     * @return <code>Field</code>数组
     */
    public static Field[] getAllInstanceFields(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return _getAllInstanceFields(clazz);
    }

    /**
     * 要求参数不为<code>null</code>
     * <p>
     * 获取类及父类的所有非静态<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * 
     * @param clazz 要获取的类
     * @return <code>Field</code>数组
     */
    static Field[] _getAllInstanceFields(Class<?> clazz) {
        List<Field> fields = CollectionUtil.createArrayList();
        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            for (Field field : itr.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            itr = itr.getSuperclass();
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * 要求参数不为<code>null</code>
     * <p>
     * 获取类及父类的所有<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * 
     * @param clazz 要获取的类
     * @return <code>Field</code>数组
     */
    static Field[] _getAllFieldsOfClass(Class<?> clazz) {
        Field[] fields = null;

        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            fields = (Field[]) ArrayUtil.addAll(itr.getDeclaredFields(), fields);
            itr = itr.getSuperclass();
        }

        return fields;
    }

    /**
     * 获取类及父类的所有<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param accessible 设置访问权限
     * @return <code>Field</code>数组
     */
    public static Field[] getAllFieldsOfClass(Class<?> clazz, boolean accessible) {
        Field[] fields = getAllFieldsOfClass(clazz);
        if (ArrayUtil.isNotEmpty(fields)) {
            AccessibleObject.setAccessible(fields, accessible);
        }

        return fields;
    }

    /**
     * 获取对象的所有<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * <p>
     * 如果<code>object</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param object 要获取的对象
     * @return <code>Field</code>数组
     */
    public static Field[] getAllFieldsOfClass(Object object) {
        if (object == null) {
            return null;
        }

        Field[] fields = _getAllFieldsOfClass(object.getClass());
        return fields;
    }

    /**
     * 获取对象的所有<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>*
     * <p>
     * 如果<code>object</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param object 要获取的对象
     * @param accessible 设置访问权限
     * @return <code>Field</code>数组
     */
    public static Field[] getAllFieldsOfClass(Object object, boolean accessible) {
        if (object == null) {
            return null;
        }

        Field[] fields = getAllFieldsOfClass(object);
        AccessibleObject.setAccessible(fields, accessible);
        return fields;
    }

    /**
     * 获取类及父类的所有非静态<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param accessible 设置访问权限
     * @return <code>Field</code>数组
     */
    public static Field[] getAllInstanceFields(Class<?> clazz, boolean accessible) {
        Field[] fields = getAllInstanceFields(clazz);
        if (ArrayUtil.isNotEmpty(fields)) {
            AccessibleObject.setAccessible(fields, accessible);
        }

        return fields;
    }

    /**
     * 获取对象的所有非静态<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * <p>
     * 如果<code>object</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param object 要获取的对象
     * @return <code>Field</code>数组
     */
    public static Field[] getAllInstanceFields(Object object) {
        if (object == null) {
            return null;
        }

        Field[] fields = _getAllInstanceFields(object.getClass());
        return fields;
    }

    /**
     * 获取对象的所有非静态<code>Field</code>，不包括<code>Object</code>的 <code>Field</code>
     * <p>
     * 如果<code>object</code>为<code>null</code>，返回<code>null</code>
     * 
     * @param object 要获取的对象
     * @param accessible 设置访问权限
     * @return <code>Field</code>数组
     */
    public static Field[] getAllInstanceFields(Object object, boolean accessible) {
        if (object == null) {
            return null;
        }

        Field[] fields = getAllInstanceFields(object);
        AccessibleObject.setAccessible(fields, accessible);
        return fields;
    }

    // FIXME
    public static Field[] getInstanceFields(Class<?> clazz, String[] includes) {
        if (clazz == null) {
            return null;
        }

        return _getInstanceFields(clazz, includes);
    }

    static Field[] _getInstanceFields(Class<?> clazz, String[] includes) {
        List<Field> fields = CollectionUtil.createArrayList();
        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            for (Field field : itr.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && ArrayUtil.contains(includes, field.getName())) {
                    fields.add(field);
                }
            }
            itr = itr.getSuperclass();
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * 获取所有包含指定<code>Annotation</code>的<code>Field</code>数组
     * 
     * @param clazz 查找类
     * @param annotationClass 注解类名
     * @return <code>Field</code>数组
     */
    public static Field[] getAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return null;
        }

        Field[] fields = _getAllFieldsOfClass(clazz);
        if (ArrayUtil.isEmpty(fields)) {
            return null;
        }

        List<Field> list = CollectionUtil.createArrayList();
        for (Field field : fields) {
            if (null != field.getAnnotation(annotationClass)) {
                list.add(field);
                field.setAccessible(true);
            }
        }

        return list.toArray(new Field[0]);
    }

    /**
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param object 要获取的对象
     * @param fieldName <code>Field</code>名
     * @return <code>Field</code>
     */
    public static Field getField(Object object, String fieldName) {
        if (ObjectUtil.isAnyNull(object, fieldName)) {
            return null;
        }

        return _getField(object.getClass(), fieldName);
    }

    /**
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param fieldName <code>Field</code>名
     * @return <code>Field</code>
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if (ObjectUtil.isAnyNull(clazz, fieldName)) {
            return null;
        }

        return _getField(clazz, fieldName);
    }

    static Field _getField(Class<?> clazz, String fieldName) {
        for (Class<?> itr = clazz; hasSuperClass(itr);) {
            Field[] fields = itr.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }

            itr = itr.getSuperclass();
        }

        return null;
    }

    /**
     * 此方法存在性能问题 FIXME，replace with {@link #getField(Object, String)}
     * <p>
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param object 要获取的对象
     * @param fieldName <code>Field</code>名
     * @return <code>Field</code>
     */
    @Deprecated
    public static Field getFieldOfClass(Object object, String fieldName) {
        if (ObjectUtil.isAnyNull(object, fieldName)) {
            return null;
        }

        return _getFieldOfClass(object.getClass(), fieldName);
    }

    /**
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param object 要获取的对象
     * @param fieldName <code>Field</code>名
     * @param accessible 设置访问权限
     * @return <code>Field</code>
     */
    public static Field getField(Object object, String fieldName, boolean accessible) {
        Field field = getField(object, fieldName);
        if (field != null) {
            field.setAccessible(accessible);
        }

        return field;
    }

    /**
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param fieldName <code>Field</code>名
     * @param accessible 设置访问权限
     * @return <code>Field</code>
     */
    public static Field getField(Class<?> clazz, String fieldName, boolean accessible) {
        Field field = getField(clazz, fieldName);
        if (field != null) {
            field.setAccessible(accessible);
        }

        return field;
    }

    /**
     * 此方法存在性能问题 FIXME，replace with {@link #getField(Class, String)}
     * <p>
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param fieldName <code>Field</code>名
     * @return <code>Field</code>
     */
    @Deprecated
    public static Field getFieldOfClass(Class<?> clazz, String fieldName) {
        if (ObjectUtil.isAnyNull(clazz, fieldName)) {
            return null;
        }

        return _getFieldOfClass(clazz, fieldName);
    }

    /**
     * 此方法存在性能问题 FIXME，replace with {@link #getField(Object, String, boolean)}
     * <p>
     * 根据对象的<code>Field</code>名返回<code>Field</code>
     * 
     * @param object 要获取的对象
     * @param fieldName <code>Field</code>名
     * @param accessible 设置访问权限
     * @return <code>Field</code>
     */
    @Deprecated
    public static Field getFieldOfClass(Object object, String fieldName, boolean accessible) {
        Field field = getFieldOfClass(object, fieldName);
        if (field != null) {
            field.setAccessible(accessible);
        }

        return field;
    }

    /**
     * 此方法存在性能问题 FIXME，replace with {@link #_getField(Class, String)}
     * <p>
     * 要求参数<code>clazz</code>不为<code>null</code>
     * <p>
     * 根据类的<code>Field</code>名返回 <code>Field</code>
     * 
     * @param clazz 要获取的类
     * @param fieldName <code>Field</code>名
     * @return <code>Field</code>
     */
    @Deprecated
    static Field _getFieldOfClass(Class<?> clazz, String fieldName) {
        Field[] fields = _getAllFieldsOfClass(clazz);
        for (Field field : fields) {
            if (field.getName().equals(fieldName))
                return field;
        }

        return null;
    }

    public static Field[] getAccessibleFields(Class<?> clazz) {
        return getAccessibleFields(clazz, Object.class);
    }

    public static Field[] getAccessibleFields(Class<?> clazz, Class<?> limit) {
        if (clazz == null) {
            return null;
        }

        Package topPackage = clazz.getPackage();
        List<Field> fieldList = CollectionUtil.createArrayList();
        int topPackageHash = (topPackage == null) ? 0 : topPackage.hashCode();
        boolean top = true;
        do {
            if (clazz == null) {
                break;
            }
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (top == true) { // add all top declared fields
                    fieldList.add(field);
                    continue;
                }
                int modifier = field.getModifiers();
                if (Modifier.isPrivate(modifier)) {
                    continue;
                }
                if (Modifier.isPublic(modifier) || Modifier.isProtected(modifier)) {
                    addFieldIfNotExist(fieldList, field);
                    continue;
                }

                // add super default methods from the same package
                Package pckg = field.getDeclaringClass().getPackage();
                int pckgHash = (pckg == null) ? 0 : pckg.hashCode();
                if (pckgHash == topPackageHash) {
                    addFieldIfNotExist(fieldList, field);
                }
            }
            top = false;
        } while ((clazz = clazz.getSuperclass()) != limit);

        Field[] fields = new Field[fieldList.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fieldList.get(i);
        }

        return fields;
    }

    private static void addFieldIfNotExist(List<Field> allFields, Field newField) {
        for (Field field : allFields) {
            if (ObjectUtil.isEquals(field, newField)) {
                return;
            }
        }

        allFields.add(newField);
    }

    /**
     * 读取<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>
     * @param target 目标对象
     * @return <code>Field</code>值
     */
    public static <T> T readField(Field field, Object target) {
        if (field == null || target == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T result = (T) _readField(field, target);

            return result;
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }
    }

    /**
     * 要求参数参数不为<code>null</code>
     * <p>
     * 读取<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>
     * @param target 目标对象
     * @return <code>Field</code>值
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    static Object _readField(Field field, Object target) throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field.get(target);
    }

    /**
     * 读取<code>Field</code>的值
     * 
     * @param fieldName 目标<code>Field</code>名
     * @param target 目标对象
     * @return <code>Field</code>值
     */
    public static Object readField(String fieldName, Object target) {
        Field field = getField(target, fieldName);
        if (field == null) {
            return null;
        }

        try {
            return _readField(field, target);
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }
    }

    /**
     * 写入<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>
     * @param target 目标对象
     * @param value 写入的值
     */
    public static void writeField(Field field, Object target, Object value) {
        if (ObjectUtil.isAllNull(field, target, value)) {
            return;
        }

        try {
            _writeField(field, target, value);
        } catch (Exception ex) {
            throw ExceptionUtil.toRuntimeException(ex);
        }
    }

    /**
     * 要求参数参数不为<code>null</code>
     * <p>
     * 写入<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>
     * @param target 目标对象
     * @param value 写入的值
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    static void _writeField(Field field, Object target, Object value) throws IllegalArgumentException,
            IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        field.set(target, value);
    }

    /**
     * 写入<code>Field</code>的值
     * 
     * @param target 目标对象
     * @param fieldName 目标<code>Field</code>名
     * @param value 写入的值
     */
    public static void writeField(Object target, String fieldName, Object value) {
        Field field = getField(target, fieldName);
        if (field != null)
            try {
                _writeField(field, target, value);
            } catch (Exception ex) {
                throw ExceptionUtil.toRuntimeException(ex);
            }
    }

    /**
     * 读取静态<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>名
     * @return <code>Field</code>值
     */
    public static Object readStaticField(Field field) {
        if (field == null) {
            return null;
        }

        Assert.assertTrue(Modifier.isStatic(field.getModifiers()), "The field '{}' is not static", field.getName());

        return readField(field, (Object) null);
    }

    /**
     * 读取静态<code>Field</code>的值
     * 
     * @param clazz 目标<code>Class</code>名
     * @return fieldName <code>Field</code>名
     * @throws FieldException
     */
    public static Object readStaticField(Class<?> clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (field == null) {
            return null;
        }

        return readStaticField(field);
    }

    /**
     * 写入<code>Field</code>的值
     * 
     * @param field 目标<code>Field</code>
     * @param value 写入的值
     * @throws FieldException
     */
    public static void writeStaticField(Field field, Object value) {
        if (field == null) {
            return;
        }

        Assert.assertTrue(Modifier.isStatic(field.getModifiers()), "The field '{}' is not static", field.getName());

        writeField(field, (Object) null, value);
    }

    /**
     * 写入<code>Field</code>的值
     * 
     * @param clazz 目标<code>Class</code>
     * @return fieldName <code>Field</code>名
     * @param value 写入的值
     * @throws FieldException
     */
    public static void writeStaticField(Class<?> clazz, String fieldName, Object value) {
        Field field = getField(clazz, fieldName);
        if (field == null) {
            return;
        }

        writeStaticField(field, value);
    }

    /**
     * <code>Field</code>是否不用改写
     * 
     * @param field 目标<code>Field</code>
     * @return 如果是则返回<code>true</code>，否则返回<code>false</code>
     */
    public static boolean notWriter(Field field) {
        return field == null || Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return field != null && Modifier.isFinal(field.getModifiers());
    }

    /**
     * 判断是否有超类
     * 
     * @param clazz 目标类
     * @return 如果有返回<code>true</code>，否则返回<code>false</code>
     */
    public static boolean hasSuperClass(Class<?> clazz) {
        return (clazz != null) && !clazz.equals(Object.class);
    }

    public static <A extends Annotation> boolean hasAnnotation(Field field, Class<A> annotationType) {
        if (ObjectUtil.isAnyNull(field, annotationType)) {
            return false;
        }

        return field.getAnnotation(annotationType) != null;
    }

    // ==========================================================================
    // Constructor相关的方法。
    // ==========================================================================

    // TODO

    // ==========================================================================
    // 泛型相关的方法。
    // ==========================================================================
    public static Class<?> getComponentType(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getComponentType(clazz.getGenericSuperclass(), null);
    }

    public static Class<?> getComponentType(Class<?> clazz, int index) {
        if (clazz == null) {
            return null;
        }

        Class<?>[] classes = getComponentTypes(clazz.getGenericSuperclass(), null);
        return classes[index];
    }

    public static Class<?>[] getComponentTypes(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getComponentTypes(clazz.getGenericSuperclass(), null);
    }

    public static Class<?>[] getComponentTypesRecursion(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        Class<?>[] classes = getComponentTypes(clazz.getGenericSuperclass(), null);
        if (ArrayUtil.isNotEmpty(classes)) {
            return classes;
        }

        return getComponentTypesRecursion(clazz.getSuperclass());
    }

    public static Class<?> getComponentTypeRecursion(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        Class<?>[] classes = getComponentTypes(clazz.getGenericSuperclass(), null);
        if (ArrayUtil.isNotEmpty(classes)) {
            return classes[classes.length - 1];
        }

        return getComponentTypeRecursion(clazz.getSuperclass());
    }

    public static Class<?> getComponentTypeRecursion(Class<?> clazz, int index) {
        if (clazz == null) {
            return null;
        }

        Class<?>[] classes = getComponentTypes(clazz.getGenericSuperclass(), null);
        if (ArrayUtil.isNotEmpty(classes)) {
            return classes[index];
        }

        return getComponentTypeRecursion(clazz.getSuperclass(), index);
    }

    /**
     * 获取<code>type</code>的泛型 ，如果<code>type</code>有多个泛型，那么将返回最后一个
     * <p>
     * 如果<code>type</code>不是泛型，则返还<code>null</code>
     * 
     * @param type 给定类型 @see Type
     * @return 获取<code>type</code>的泛型
     */
    public static Class<?> getComponentType(Type type) {
        return getComponentType(type, null);
    }

    public static Class<?> getComponentType(Type type, Class<?> implClass) {
        Class<?>[] componentTypes = getComponentTypes(type, implClass);
        if (componentTypes == null) {
            return null;
        }
        return componentTypes[componentTypes.length - 1];
    }

    public static Class<?>[] getComponentTypes(Type type) {
        return getComponentTypes(type, null);
    }

    /**
     * 获取<code>type</code>的第<code>index</code>个泛型 ，如果<code>index</code>超长， 那么将返回最后一个
     * <p>
     * 如果是数组类型，则忽略<code>index</code>
     * <p>
     * 如果<code>type</code>不是泛型，则返还<code>null</code>
     * <p>
     * 如下均为组合类型：
     * <ul>
     * <li>MyClass[]</li>
     * <li>List&lt;MyClass&gt;</li>
     * <li>Foo&lt;? extends MyClass&gt;</li>
     * <li>Bar&lt;? super MyClass&gt;</li>
     * <li>&lt;T extends MyClass&gt; T[]</li>
     * </ul>
     * 
     * @param type 给定类型 @see Type
     * @param index 泛型的索引位
     * @return 获取<code>type</code>的第<code>index</code>个泛型
     */
    public static Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
        if (type == null) {
            return null;
        }

        GenericType gt = GenericType.find(type);
        if (gt != null) {
            return gt.getComponentTypes(type, implClass);
        }

        return null;

    }

    public static Class<?>[] getGenericSuperTypes(Class<?> type) {
        if (type == null) {
            return null;
        }

        return getComponentTypes(type.getGenericSuperclass());
    }

    public static Class<?> getGenericSuperType(Class<?> type) {
        if (type == null) {
            return null;
        }

        Class<?>[] componentTypes = getComponentTypes(type.getGenericSuperclass());

        if (componentTypes == null) {
            return null;
        }

        return componentTypes[0];
    }

    public static Class<?> getRawType(Type type) {
        return getRawType(type, null);
    }

    public static Class<?> getRawType(Type type, Class<?> implClass) {
        if (type == null) {
            return null;
        }

        GenericType gt = GenericType.find(type);
        if (gt != null) {
            return gt.toRawType(type, implClass);
        }

        return null;

    }

    enum GenericType {

        CLASS_TYPE {

            @Override
            Class<?> type() {
                return Class.class;
            }

            @Override
            Class<?> toRawType(Type type, Class<?> implClass) {
                return (Class<?>) type;
            }

            @Override
            Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isArray()) {
                    return new Class[] { clazz.getComponentType() };
                }
                return null;
            }
        },
        PARAMETERIZED_TYPE {

            @Override
            Class<?> type() {
                return ParameterizedType.class;
            }

            @Override
            Class<?> toRawType(Type type, Class<?> implClass) {
                ParameterizedType pType = (ParameterizedType) type;
                return getRawType(pType.getRawType(), implClass);
            }

            @Override
            Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
                ParameterizedType pt = (ParameterizedType) type;

                Type[] generics = pt.getActualTypeArguments();

                if (generics.length == 0) {
                    return null;
                }

                Class<?>[] types = new Class[generics.length];

                for (int i = 0; i < generics.length; i++) {
                    types[i] = getRawType(generics[i], implClass);
                }
                return types;
            }
        },
        WILDCARD_TYPE {

            @Override
            Class<?> type() {
                return WildcardType.class;
            }

            @Override
            Class<?> toRawType(Type type, Class<?> implClass) {
                WildcardType wType = (WildcardType) type;

                Type[] lowerTypes = wType.getLowerBounds();
                if (lowerTypes.length > 0) {
                    return getRawType(lowerTypes[0], implClass);
                }

                Type[] upperTypes = wType.getUpperBounds();
                if (upperTypes.length != 0) {
                    return getRawType(upperTypes[0], implClass);
                }

                return Object.class;
            }

            @Override
            Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
                return null;
            }
        },
        GENERIC_ARRAY_TYPE {

            @Override
            Class<?> type() {
                return GenericArrayType.class;
            }

            @Override
            Class<?> toRawType(Type type, Class<?> implClass) {
                Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
                Class<?> rawType = getRawType(genericComponentType, implClass);
                // FIXME
                return Array.newInstance(rawType, 0).getClass();
            }

            @Override
            Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
                GenericArrayType gat = (GenericArrayType) type;

                Class<?> rawType = getRawType(gat.getGenericComponentType(), implClass);
                if (rawType == null) {
                    return null;
                }

                return new Class[] { rawType };
            }
        },
        TYPE_VARIABLE {

            @Override
            Class<?> type() {
                return TypeVariable.class;
            }

            @Override
            Class<?> toRawType(Type type, Class<?> implClass) {
                TypeVariable<?> varType = (TypeVariable<?>) type;
                if (implClass != null) {
                    Type resolvedType = resolveVariable(varType, implClass);
                    if (resolvedType != null) {
                        return getRawType(resolvedType, null);
                    }
                }
                Type[] boundsTypes = varType.getBounds();
                if (boundsTypes.length == 0) {
                    return Object.class;
                }
                return getRawType(boundsTypes[0], implClass);
            }

            @Override
            Class<?>[] getComponentTypes(Type type, Class<?> implClass) {
                return null;
            }
        };

        abstract Class<?> toRawType(Type type, Class<?> implClass);

        abstract Class<?> type();

        abstract Class<?>[] getComponentTypes(Type type, Class<?> implClass);

        static GenericType find(Type type) {
            for (GenericType gt : GenericType.values()) {
                if (ClassUtil.isInstance(gt.type(), type)) {
                    return gt;
                }
            }

            return null;
        }
    }

    public static Type resolveVariable(TypeVariable<?> variable, final Class<?> implClass) {
        final Class<?> rawType = getRawType(implClass, null);

        int index = ArrayUtil.indexOf(rawType.getTypeParameters(), variable);
        if (index >= 0) {
            return variable;
        }

        final Class<?>[] interfaces = rawType.getInterfaces();
        final Type[] genericInterfaces = rawType.getGenericInterfaces();

        for (int i = 0; i <= interfaces.length; i++) {
            Class<?> rawInterface;

            if (i < interfaces.length) {
                rawInterface = interfaces[i];
            } else {
                rawInterface = rawType.getSuperclass();
                if (rawInterface == null) {
                    continue;
                }
            }

            final Type resolved = resolveVariable(variable, rawInterface);
            if (resolved instanceof Class || resolved instanceof ParameterizedType) {
                return resolved;
            }

            if (resolved instanceof TypeVariable) {
                final TypeVariable<?> typeVariable = (TypeVariable<?>) resolved;
                index = ArrayUtil.indexOf(rawInterface.getTypeParameters(), typeVariable);

                if (index < 0) {
                    throw new IllegalArgumentException("Invalid type variable:" + typeVariable);
                }

                final Type type = i < genericInterfaces.length ? genericInterfaces[i] : rawType.getGenericSuperclass();

                if (type instanceof Class) {
                    return Object.class;
                }

                if (type instanceof ParameterizedType) {
                    return ((ParameterizedType) type).getActualTypeArguments()[index];
                }

                throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
        return null;
    }

    // ==========================================================================
    // 辅助方法。
    // ==========================================================================

    private static final Method IS_SYNTHETIC;
    static {
        Method isSynthetic = null;
        if (SystemUtil.getJavaInfo().isJavaVersionAtLeast(1.5f)) {
            // cannot call synthetic methods:
            try {
                isSynthetic = Member.class.getMethod("isSynthetic", Emptys.EMPTY_CLASS_ARRAY);
            } catch (Exception e) {
                // ignore
            }
        }
        IS_SYNTHETIC = isSynthetic;
    }

    public static boolean isAccessible(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !isSynthetic(m);
    }

    static boolean isSynthetic(Member m) {
        if (IS_SYNTHETIC != null) {
            try {
                return ((Boolean) IS_SYNTHETIC.invoke(m, (Object[]) null)).booleanValue();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static boolean isPublic(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers());
    }

    public static void forceAccess(AccessibleObject object) {
        if (object == null || object.isAccessible()) {
            return;
        }
        try {
            object.setAccessible(true);
        } catch (SecurityException e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

}
