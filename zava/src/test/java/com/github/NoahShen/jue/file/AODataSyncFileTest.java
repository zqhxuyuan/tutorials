/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.io.File;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author noah
 *
 */
public class AODataSyncFileTest {
	
	private AODataSyncFile aoDataSyncFile;
	
	@Before
	public void setUp() throws Exception {
		File file = new File("/tmp/AODataSyncFile");
		aoDataSyncFile = new AODataSyncFile(file, AODataSyncFile.BUFFER_STRATEGY);
	}

	@After
	public void tearDown() throws Exception {
		aoDataSyncFile.close();
		File file = new File("/tmp/AODataSyncFile");
		file.delete();
	}
	
	//@Test
	public void testWriteData() throws Exception {
		byte[] headerBytes = new byte[FileHeader.HEADER_SIZE];
		for (int i = 0; i < headerBytes.length; ++i) {
			headerBytes[i] = 8;
		}
		
		for (int i = 0; i < 6; ++i) {
			byte[] dataBytes = createDataBytes((byte) i, 1024);
			aoDataSyncFile.appendData(ByteBuffer.wrap(headerBytes), ByteBuffer.wrap(dataBytes));
		}
	}
	
	private byte[] createDataBytes(byte content, int len) {
		byte[] dataBytes = new byte[len];
		for (int i = 0; i < dataBytes.length; ++i) {
			dataBytes[i] = content;
		}
		return dataBytes;
	}
	
	@Test
	public void testReadData() throws Exception {
		testWriteData();
		int bufferSize = 1024;
		for (int i = 0; i < 6; ++i) {
			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			aoDataSyncFile.read(readBuffer, FileHeader.HEADER_SIZE + i * bufferSize);
			readBuffer.flip();
			Assert.assertEquals((byte) i, readBuffer.get());
		}
	}
}
