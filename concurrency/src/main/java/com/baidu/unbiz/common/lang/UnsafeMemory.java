///**
// * 
// */
//package com.baidu.unbiz.common.lang;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.Field;
//import java.util.concurrent.ConcurrentMap;
//
//import org.apache.commons.lang.ArrayUtils;
//
//import sun.misc.Unsafe;
//
//import com.baidu.unbiz.common.ClassUtil;
//import com.baidu.unbiz.common.CollectionUtil;
//import com.baidu.unbiz.common.cache.FieldCache;
//
///**
// * FIXME 持续集成环境编译通不过，可能和本地库有关，先屏蔽
// * <p>
// * 直接内存操作，编解码神器。 FIXME (没完成)
// * 
// * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
// * @version create on 2014年7月24日 上午12:47:18
// */
//public class UnsafeMemory {
//
//	private static final int defaultCapacity = 1024;
//
//	private static final long defaultOffset = 8;
//
//	private static final Unsafe unsafe;
//	static {
//		try {
//			Field field = Unsafe.class.getDeclaredField("theUnsafe");
//			field.setAccessible(true);
//			unsafe = (Unsafe) field.get(null);
//		} catch (Exception e) {
//			throw new IllegalStateException(
//					"This JVM has no sun.misc.Unsafe support, "
//							+ "please choose another MemoryManager implementation");
//		}
//	}
//
//	private static final long byteArrayOffset = unsafe
//			.arrayBaseOffset(byte[].class);
//	private static final long booleanArrayOffset = unsafe
//			.arrayBaseOffset(boolean[].class);
//	private static final long shortArrayOffset = unsafe
//			.arrayBaseOffset(short[].class);
//	private static final long charArrayOffset = unsafe
//			.arrayBaseOffset(char[].class);
//	private static final long intArrayOffset = unsafe
//			.arrayBaseOffset(int[].class);
//	private static final long floatArrayOffset = unsafe
//			.arrayBaseOffset(float[].class);
//	private static final long longArrayOffset = unsafe
//			.arrayBaseOffset(long[].class);
//	private static final long doubleArrayOffset = unsafe
//			.arrayBaseOffset(double[].class);
//
//	private static final int SIZE_OF_BOOLEAN = 1;
//	private static final int SIZE_OF_BYTE = 1;
//	private static final int SIZE_OF_SHORT = 2;
//	private static final int SIZE_OF_CHAR = 2;
//	private static final int SIZE_OF_INT = 4;
//	private static final int SIZE_OF_FLOAT = 4;
//	private static final int SIZE_OF_LONG = 8;
//	private static final int SIZE_OF_DOUBLE = 8;
//
//	private static final ConcurrentMap<Class<?>, Long> classOffsetMap = CollectionUtil
//			.createConcurrentMap();
//
//	private int pos = 0;
//	private byte[] buffer;
//
//	static {
//		classOffsetMap.put(Byte.class, defaultOffset);
//		classOffsetMap.put(Boolean.class, defaultOffset);
//		classOffsetMap.put(Short.class, defaultOffset);
//		classOffsetMap.put(Character.class, defaultOffset);
//		classOffsetMap.put(Integer.class, defaultOffset);
//		classOffsetMap.put(Float.class, defaultOffset);
//		classOffsetMap.put(Long.class, defaultOffset);
//		classOffsetMap.put(Double.class, defaultOffset);
//		classOffsetMap.put(Object.class, defaultOffset);
//	}
//
//	public UnsafeMemory() {
//		this(new byte[defaultCapacity]);
//	}
//
//	public UnsafeMemory(final byte[] buffer) {
//		if (null == buffer) {
//			throw new NullPointerException("buffer cannot be null");
//		}
//
//		this.buffer = buffer;
//	}
//
//	public UnsafeMemory(int capacity) {
//		if (capacity <= 0) {
//			capacity = defaultCapacity;
//		}
//		this.buffer = new byte[capacity];
//	}
//
//	public void reset() {
//		this.pos = 0;
//	}
//
//	public void putBoolean(final boolean value) {
//		ensureCapacity(SIZE_OF_BOOLEAN);
//		unsafe.putBoolean(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_BOOLEAN;
//	}
//
//	public boolean getBoolean() {
//		boolean value = unsafe.getBoolean(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_BOOLEAN;
//
//		return value;
//	}
//
//	public void putByte(final byte value) {
//		ensureCapacity(SIZE_OF_BYTE);
//		unsafe.putByte(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_BYTE;
//	}
//
//	public byte getByte() {
//		byte value = unsafe.getByte(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_BYTE;
//
//		return value;
//	}
//
//	public void putShort(final short value) {
//		ensureCapacity(SIZE_OF_SHORT);
//		unsafe.putShort(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_SHORT;
//	}
//
//	public short getShort() {
//		short value = unsafe.getShort(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_SHORT;
//
//		return value;
//	}
//
//	public void putChar(final char value) {
//		ensureCapacity(SIZE_OF_CHAR);
//		unsafe.putChar(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_CHAR;
//	}
//
//	public char getChar() {
//		char value = unsafe.getChar(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_CHAR;
//
//		return value;
//	}
//
//	public void putInt(final int value) {
//		ensureCapacity(SIZE_OF_INT);
//		unsafe.putInt(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_INT;
//	}
//
//	public int getInt() {
//		int value = unsafe.getInt(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_INT;
//
//		return value;
//	}
//
//	public void putFloat(final float value) {
//		ensureCapacity(SIZE_OF_FLOAT);
//		unsafe.putFloat(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_FLOAT;
//	}
//
//	public float getFloat() {
//		float value = unsafe.getFloat(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_FLOAT;
//
//		return value;
//	}
//
//	public void putLong(final long value) {
//		ensureCapacity(SIZE_OF_LONG);
//		unsafe.putLong(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_LONG;
//	}
//
//	public long getLong() {
//		long value = unsafe.getLong(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_LONG;
//
//		return value;
//	}
//
//	public void putDouble(final double value) {
//		ensureCapacity(SIZE_OF_DOUBLE);
//		unsafe.putDouble(buffer, byteArrayOffset + pos, value);
//		pos += SIZE_OF_DOUBLE;
//	}
//
//	public double getDouble() {
//		double value = unsafe.getDouble(buffer, byteArrayOffset + pos);
//		pos += SIZE_OF_DOUBLE;
//
//		return value;
//	}
//
//	public void putString(final String value) {
//		// FIXME
//		if (value == null) {
//			return;
//		}
//		byte[] values = value.getBytes();
//		putByteArray(values);
//	}
//
//	public String getString() {
//		byte[] values = getByteArray();
//		// FIXME
//		if (values == null) {
//			return null;
//		}
//		return new String(values);
//	}
//
//	public <T> void putObject(final T value) {
//		// FIXME
//		if (value == null) {
//			return;
//		}
//		Class<?> clazz = value.getClass();
//		long offset = 0;
//		if (classOffsetMap.containsKey(clazz)) {
//			offset = classOffsetMap.get(clazz);
//		} else {
//			offset = classOffset(clazz);
//		}
//		ensureCapacity(offset);
//		unsafe.putObject(buffer, byteArrayOffset + pos, value);
//		pos += offset;
//		classOffsetMap.putIfAbsent(clazz, offset);
//	}
//
//	public <T> T getObject() {
//		return getObject(null);
//	}
//
//	public <T> T getObject(final Class<T> clazz) {
//		T value = clazz.cast(unsafe.getObject(buffer, byteArrayOffset + pos));
//		// FIXME
//		if (value == null) {
//			return null;
//		}
//		Class<?> realClass = (clazz == null) ? value.getClass() : clazz;
//
//		long offset = getOffset(realClass);
//		pos += offset;
//		return value;
//	}
//
//	public void putBooleanArray(final boolean[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length;
//		unsafe.copyMemory(values, booleanArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public boolean[] getBooleanArray() {
//		int arraySize = getInt();
//		boolean[] values = new boolean[arraySize];
//
//		long bytesToCopy = values.length;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				booleanArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putByteArray(final byte[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length;
//		unsafe.copyMemory(values, byteArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public byte[] getByteArray() {
//		int arraySize = getInt();
//		byte[] values = new byte[arraySize];
//
//		long bytesToCopy = values.length;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				byteArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putShortArray(final short[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 1;
//		unsafe.copyMemory(values, shortArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public short[] getShortArray() {
//		int arraySize = getInt();
//		short[] values = new short[arraySize];
//
//		long bytesToCopy = values.length << 1;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				shortArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putCharArray(final char[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 1;
//		ensureCapacity(bytesToCopy);
//		unsafe.copyMemory(values, charArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public char[] getCharArray() {
//		int arraySize = getInt();
//		char[] values = new char[arraySize];
//
//		long bytesToCopy = values.length << 1;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				charArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putIntArray(final int[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 2;
//		ensureCapacity(bytesToCopy);
//		unsafe.copyMemory(values, intArrayOffset, buffer,
//				byteArrayOffset + pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public int[] getIntArray() {
//		int arraySize = getInt();
//		int[] values = new int[arraySize];
//
//		long bytesToCopy = values.length << 2;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				intArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putFloatArray(final float[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 2;
//		ensureCapacity(bytesToCopy);
//		unsafe.copyMemory(values, floatArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public float[] getFloatArray() {
//		int arraySize = getInt();
//		float[] values = new float[arraySize];
//
//		long bytesToCopy = values.length << 2;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				floatArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putLongArray(final long[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 3;
//		ensureCapacity(bytesToCopy);
//		unsafe.copyMemory(values, longArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public long[] getLongArray() {
//		int arraySize = getInt();
//		long[] values = new long[arraySize];
//
//		long bytesToCopy = values.length << 3;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				longArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putDoubleArray(final double[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//
//		long bytesToCopy = values.length << 3;
//		ensureCapacity(bytesToCopy);
//		unsafe.copyMemory(values, doubleArrayOffset, buffer, byteArrayOffset
//				+ pos, bytesToCopy);
//		pos += bytesToCopy;
//	}
//
//	public double[] getDoubleArray() {
//		int arraySize = getInt();
//		double[] values = new double[arraySize];
//
//		long bytesToCopy = values.length << 3;
//		unsafe.copyMemory(buffer, byteArrayOffset + pos, values,
//				doubleArrayOffset, bytesToCopy);
//		pos += bytesToCopy;
//
//		return values;
//	}
//
//	public void putStringArray(final String[] values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//		for (String value : values) {
//			putString(value);
//		}
//	}
//
//	public String[] getStringArray() {
//		int arraySize = getInt();
//		String[] values = new String[arraySize];
//		for (int i = 0; i < arraySize; i++) {
//			values[i] = getString();
//		}
//
//		return values;
//	}
//
//	public <T> void putObjectArray(final T... values) {
//		if (values == null) {
//			return;
//		}
//		putInt(values.length);
//		for (T object : values) {
//			putObject(object);
//		}
//	}
//
//	// public <T> void putAnyArray(final T... values) {
//	// if (values == null) {
//	// return;
//	// }
//	// Class<?> itemClass = values[0].getClass();
//	// if (itemClass.equals(Integer.TYPE)) {
//	// this.putIntArray(()values);
//	// return;
//	// }
//	// }
//
//	public Object[] getObjectArray() {
//		int arraySize = getInt();
//		Object[] values = new Object[arraySize];
//		for (int i = 0; i < arraySize; i++) {
//			values[i] = getObject();
//		}
//
//		return values;
//	}
//
//	// public <T> T[] getObjectArray(Class<T> clazz) {
//	// int arraySize = getInt();
//	// @SuppressWarnings("unchecked")
//	// T[] values = (T[]) Array.newInstance(clazz, arraySize);
//	// for (int i = 0; i < arraySize; i++) {
//	// values[i] = getObject();
//	// }
//	//
//	// return values;
//	// }
//
//	public Object[] getObjectArray(Class<?> clazz) {
//		int arraySize = getInt();
//		Object[] values = (Object[]) Array.newInstance(clazz, arraySize);
//		for (int i = 0; i < arraySize; i++) {
//			values[i] = getObject();
//		}
//
//		return values;
//	}
//
//	private static long classOffset(Class<?> clazz) {
//		long offset = 0;
//		Field[] fields = FieldCache.getInstance().getInstanceFields(clazz);
//		if (ArrayUtils.isEmpty(fields)) {
//			return defaultOffset;
//		}
//		for (Field field : fields) {
//			offset = unsafe.objectFieldOffset(field);
//		}
//		return offset;
//	}
//
//	public static int getOffset(Class<?> clazz) {
//		if (classOffsetMap.containsKey(clazz)) {
//			return classOffsetMap.get(clazz).intValue();
//		}
//		Long offset = classOffset(clazz);
//		classOffsetMap.putIfAbsent(clazz, offset);
//		return offset.intValue();
//	}
//
//	// FIXME @see ClassUtil.PrimitiveInfo
//	public void putField(Field field, Object object, Object value) {
//		long offset = unsafe.objectFieldOffset(field);
//		Class<?> type = field.getType();
//		if (!type.isPrimitive()) {
//			unsafe.putObject(object, offset, value);
//			return;
//		}
//
//		if (type.equals(Integer.TYPE)) {
//			unsafe.putInt(object, offset, ((Integer) value).intValue());
//			return;
//		}
//		if (type.equals(Long.TYPE)) {
//			unsafe.putLong(object, offset, ((Long) value).longValue());
//			return;
//		}
//		if (type.equals(Short.TYPE)) {
//			unsafe.putShort(object, offset, ((Short) value).shortValue());
//			return;
//		}
//		if (type.equals(Character.TYPE)) {
//			unsafe.putChar(object, offset, ((Character) value).charValue());
//			return;
//		}
//		if (type.equals(Byte.TYPE)) {
//			unsafe.putByte(object, offset, ((Byte) value).byteValue());
//			return;
//		}
//		if (type.equals(Float.TYPE)) {
//			unsafe.putFloat(object, offset, ((Float) value).floatValue());
//			return;
//		}
//		if (type.equals(Double.TYPE)) {
//			unsafe.putDouble(object, offset, ((Double) value).doubleValue());
//			return;
//		}
//		if (type.equals(Boolean.TYPE)) {
//			unsafe.putBoolean(object, offset, ((Boolean) value).booleanValue());
//			return;
//		}
//	}
//
//	public void putAny(final Object value) {
//		if (value == null) {
//			return;
//		}
//		Class<?> clazz = value.getClass();
//		if (ClassUtil.isPrimitiveOrWrapper(value.getClass())) {
//			putPrimitiveOrWrapper(value);
//		}
//		if (clazz.isArray()) {
//
//		}
//	}
//
//	// FIXME @see ClassUtil.PrimitiveInfo
//	public void putPrimitiveOrWrapper(final Object value) {
//		Class<?> type = value.getClass();
//		if (type.equals(Integer.TYPE)) {
//			this.putInt(((Integer) value).intValue());
//			return;
//		}
//		if (type.equals(Long.TYPE)) {
//			this.putLong(((Long) value).longValue());
//			return;
//		}
//		if (type.equals(Short.TYPE)) {
//			this.putShort(((Short) value).shortValue());
//			return;
//		}
//		if (type.equals(Character.TYPE)) {
//			this.putChar(((Character) value).charValue());
//			return;
//		}
//		if (type.equals(Byte.TYPE)) {
//			this.putByte(((Byte) value).byteValue());
//			return;
//		}
//		if (type.equals(Float.TYPE)) {
//			this.putFloat(((Float) value).floatValue());
//			return;
//		}
//		if (type.equals(Double.TYPE)) {
//			this.putDouble(((Double) value).doubleValue());
//			return;
//		}
//		if (type.equals(Boolean.TYPE)) {
//			this.putBoolean(((Boolean) value).booleanValue());
//			return;
//		}
//	}
//
//	/**
//	 * 简单点，buffer用完后，自动增加原buffer的1/2，并且至少确保能存入完整数据
//	 */
//	private void ensureCapacity(long offset) {
//		if (pos + offset > buffer.length) {
//			Long addCapacity = (offset > buffer.length / 2) ? offset
//					: buffer.length / 2;
//			buffer = ArrayUtils
//					.addAll(buffer, new byte[addCapacity.intValue()]);
//		}
//	}
//
//	public byte[] getBuffer() {
//		return ArrayUtils.subarray(buffer, 0, pos);
//	}
//
//	public int maxCapacity() {
//		return buffer.length;
//	}
//
//	public int usedCapacity() {
//		return pos;
//	}
//
//	public int addressSize() {
//		return unsafe.addressSize();
//	}
//
// }
