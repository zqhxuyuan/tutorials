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
public class BlockFileChannelTest {
	
	private BlockFileChannel blockFileChannel;
	
	private File blockFile = new File("/tmp/blockTestFile");
	
	@Before
	public void setUp() throws Exception {
		blockFileChannel = new BlockFileChannel(blockFile, 32, new CRC32ChecksumGenerator());
	}

	@After
	public void tearDown() throws Exception {
		blockFileChannel.close();
		blockFile.delete();
	}

	
	@Test
	public void testReadData() throws Exception {
		ByteBuffer writeDataBuffer = ByteBuffer.allocate(100);
		for (int i = 0; i < 10; ++i) {
			writeDataBuffer.putInt(i);
		}
		writeDataBuffer.flip();
		blockFileChannel.write(writeDataBuffer, 0);
		
		ByteBuffer dataBuffer = ByteBuffer.allocate(40);
		int size = blockFileChannel.read(dataBuffer, 0, true);
		Assert.assertEquals(40, size);
		dataBuffer.limit(size);
		dataBuffer.flip();
		for (int i = 0; i < 10; ++i) {
			int j = dataBuffer.getInt();
			Assert.assertEquals(i, j);
		}
	}
	
}
