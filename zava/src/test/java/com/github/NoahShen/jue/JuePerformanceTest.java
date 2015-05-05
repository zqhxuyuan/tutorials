/**
 * 
 */
package com.github.NoahShen.jue;

import java.io.File;

import org.junit.Test;

import com.github.NoahShen.jue.doc.DocObject;
import com.github.NoahShen.jue.file.AODataSyncFile;

/**
 * @author noah
 *
 */
public class JuePerformanceTest {

	@Test
	public void testPut() {
		final int times = 10000;
		
		String putTestFile = "/tmp/juePutTest.jue";
		File dataFile = new File(putTestFile);
		if (dataFile.exists()) {
			dataFile.delete();
		}

		
		FileConfig config = new FileConfig();
		config.setKeyTreeMin(64);
		config.setValueRevTreeMin(64);
		config.setValueCompressed(false);
		config.setCacheCapacity(10);
		config.setDataBufferSize(AODataSyncFile.DEFAULT_MAX_DATA_BUFFER_SIZE);
		Jue jue = new Jue(putTestFile, config);
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			DocObject docObj = new DocObject();
			docObj.put("property1", true);
			jue.put(i + "", docObj, -1, false);
		}
		long escaped = System.currentTimeMillis() - startTime;
		System.out.println(String.format("put %d times, escaped: %d, %.2f times/millis", times, escaped, (double)times/(double)escaped));
		jue.close();
	}
	
	@Test
	public void testGet() {
		final int times = 10000;
		
		String getTestFile = "/tmp/jueGetTest.jue";
		File dataFile = new File(getTestFile);
		if (dataFile.exists()) {
			dataFile.delete();
		}

		
		FileConfig config = new FileConfig();
		config.setKeyTreeMin(64);
		config.setValueRevTreeMin(64);
		config.setValueCompressed(false);
		config.setCacheCapacity(10);
		config.setDataBufferSize(AODataSyncFile.DEFAULT_MAX_DATA_BUFFER_SIZE);
		Jue jue = new Jue(getTestFile, config);
		

		for (int i = 0; i < times; i++) {
			DocObject docObj = new DocObject();
			docObj.put("property1", true);
			jue.put(i + "", docObj, -1, false);
		}
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			jue.get(i + "", -1);
		}
		long escaped = System.currentTimeMillis() - startTime;
		System.out.println(String.format("get %d times, escaped: %d, %.2f times/millis", times, escaped, (double)times/(double)escaped));
		jue.close();
	}
}
