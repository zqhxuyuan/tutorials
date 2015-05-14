///**
// * 
// */
//package com.baidu.unbiz.common.lang;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.baidu.unbiz.common.logger.CachedLogger;
//import com.baidu.unbiz.common.sample.SampleSignal;
//
///**
// * FIXME
// * 
// * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
// * @version create on 2014年7月31日 下午7:37:44
// */
//public class UnsafeMemoryTest extends CachedLogger {
//
//	private UnsafeMemory unsafeMemory;
//
//	@Before
//	public void setUp() throws Exception {
//		unsafeMemory = new UnsafeMemory();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		unsafeMemory = null;
//	}
//
//	@Test
//	public void testString() {
//		unsafeMemory.putString("hello world");
//		unsafeMemory.putString("ggg");
//		unsafeMemory.reset();
//		assertEquals("hello world", unsafeMemory.getString());
//		assertEquals("ggg", unsafeMemory.getString());
//	}
//
//	private void writeObject(UnsafeMemory unsafeMemory) {
//		unsafeMemory.putObject("hello world");
//		unsafeMemory.putObject(1000);
//		unsafeMemory.putObject(new SampleSignal[] {
//				createSignal("sdsf", "greate", 170),
//				createSignal("zzz", "ffsleow", -3000) });
//		unsafeMemory.putObject(2000);
//		AppInfo appInfo = new AppInfo();
//		appInfo.setAppVersion("test_ok");
//		// appInfo.setRunScript("cp.sh");
//		unsafeMemory.putObject(appInfo);
//		unsafeMemory.putObject('我');
//		unsafeMemory.putObject(new Date());
//		unsafeMemory.putObject(createSignal("sdsf", "greate", 170));
//		unsafeMemory.putObject(new String[] { "111", "222", "555", "aaa" });
//		unsafeMemory.putObject(12.89);
//	}
//
//	public static SampleSignal createSignal(String byteString, String string,
//			long num) {
//		SampleSignal signal = new SampleSignal();
//		signal.setByteArrayField(byteString.getBytes());
//		signal.setStringField(string);
//		signal.setLongField(num);
//		return signal;
//	}
//
//	private void write(UnsafeMemory unsafeMemory) {
//		unsafeMemory.putString("hello world");
//		unsafeMemory.putInt(1000);
//		unsafeMemory.putObjectArray(createSignal("sdsf", "greate", 170),
//				createSignal("zzz", "ffsleow", -3000));
//		unsafeMemory.putInt(2000);
//		AppInfo appInfo = new AppInfo();
//		appInfo.setAppVersion("test_ok");
//		// appInfo.setRunScript("cp.sh");
//		unsafeMemory.putObject(appInfo);
//		unsafeMemory.putChar('我');
//		unsafeMemory.putObject(new Date());
//		unsafeMemory.putObject(createSignal("sdsf", "greate", 170));
//		unsafeMemory
//				.putStringArray(new String[] { "111", "222", "555", "aaa" });
//		unsafeMemory.putDouble(12.89);
//	}
//
//	private void read(UnsafeMemory unsafeMemory) {
//		// for (Object object = unsafeMemory.getObject(); object != null; object
//		// = unsafeMemory
//		// .getObject()) {
//		// logger.info(object);
//		// }
//		logger.info(unsafeMemory.getString());
//		logger.info(unsafeMemory.getInt());
//		logger.info(unsafeMemory.getObjectArray(SampleSignal.class));
//		logger.info(unsafeMemory.getInt());
//		logger.info(unsafeMemory.getObject());
//		logger.info(unsafeMemory.getChar());
//		logger.info(unsafeMemory.getObject(Date.class));
//		logger.info(unsafeMemory.getObject());
//		logger.info(unsafeMemory.getStringArray());
//		logger.info(unsafeMemory.getDouble());
//	}
//
//	private void readObject(UnsafeMemory unsafeMemory) {
//		for (Object object = unsafeMemory.getObject(); object != null; object = unsafeMemory
//				.getObject()) {
//			logger.info(object);
//		}
//	}
//
//	@Test
//	public void testMore() {
//		write(unsafeMemory);
//		unsafeMemory.reset();
//		read(unsafeMemory);
//	}
//
//	@Test
//	public void testBasic() {
//		unsafeMemory.putObject(100);
//		unsafeMemory.putObject('m');
//		unsafeMemory.putObject(1.34F);
//		unsafeMemory.putObject(6.78D);
//		unsafeMemory.putObject(Boolean.TRUE);
//		// unsafeMemory.putObject(new Object());
//		unsafeMemory.putObject(Byte.valueOf((byte) 80));
//		unsafeMemory.putObject(7777L);
//		unsafeMemory.putObject("must kill");
//		unsafeMemory.putObject(Short.valueOf("11111"));
//		unsafeMemory.reset();
//		assertEquals(100, unsafeMemory.getObject());
//		assertEquals('m', unsafeMemory.getObject());
//		assertEquals(1.34F, unsafeMemory.getObject());
//		assertEquals(6.78D, unsafeMemory.getObject());
//		assertEquals(Boolean.TRUE, unsafeMemory.getObject());
//		assertEquals(Byte.valueOf((byte) 80), unsafeMemory.getObject());
//		assertEquals(7777L, unsafeMemory.getObject());
//		assertEquals("must kill", unsafeMemory.getObject());
//		assertEquals(Short.valueOf("11111"), unsafeMemory.getObject());
//		// for (Object object = unsafeMemory.getObject(); object != null; object
//		// = unsafeMemory
//		// .getObject()) {
//		// logger.info(object);
//		// }
//	}
//
//	@Test
//	public void testSerialised() {
//		write(unsafeMemory);
//		logger.info(unsafeMemory.maxCapacity());
//		logger.info(unsafeMemory.usedCapacity());
//		logger.info("xxxxxxxxxxx to serialised xxxxxxxxxxxxx");
//		UnsafeMemory newMemory = new UnsafeMemory(unsafeMemory.getBuffer());
//		read(newMemory);
//		logger.info(newMemory.maxCapacity());
//		logger.info(newMemory.usedCapacity());
//	}
//
//	@Test
//	public void testAbstract() {
//		// unsafeMemory.putObject("hello world");
//		unsafeMemory.putObject(TimeUnit.DAYS);
//		// unsafeMemory.putString("hello world");
//		// unsafeMemory.putInt(100);
//		logger.info(Arrays.toString(unsafeMemory.getBuffer()));
//	}
//
//	@Test
//	public void testCommonSerialised() {
//		writeObject(unsafeMemory);
//		logger.info(unsafeMemory.maxCapacity());
//		logger.info(unsafeMemory.usedCapacity());
//		logger.info("xxxxxxxxxxx to serialised xxxxxxxxxxxxx");
//		UnsafeMemory newMemory = new UnsafeMemory(unsafeMemory.getBuffer());
//		readObject(newMemory);
//		logger.info(newMemory.maxCapacity());
//		logger.info(newMemory.usedCapacity());
//	}
//
//	@Test
//	public void test() {// org.slf4j
//		// unsafeMemory.putInt(4);
//		// unsafeMemory.reset();
//		// System.out.print(unsafeMemory.getInt());
//		try {
//			unsafeMemory.putObject(new AppInfo());
//			unsafeMemory.putObject("xxxxx");
//			unsafeMemory.reset();
//			logger.info(unsafeMemory.getObject());
//			logger.info(unsafeMemory.getObject());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Test
//	public void testAllocateMemory() {
//		UnsafeMemory smallMemory = new UnsafeMemory(10);
//		write(smallMemory);
//		logger.info(smallMemory.maxCapacity());
//		logger.info(smallMemory.usedCapacity());
//		logger.info("xxxxxxxxxxx to serialised xxxxxxxxxxxxx");
//		UnsafeMemory newMemory = new UnsafeMemory(smallMemory.getBuffer());
//		read(newMemory);
//		logger.info(newMemory.maxCapacity());
//		logger.info(newMemory.usedCapacity());
//	}
//
// }
