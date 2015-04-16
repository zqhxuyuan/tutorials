package com.zqh.hadoop.nimbus.test.java.nimbus.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import com.zqh.hadoop.nimbus.utils.NimbusInputStream;
import com.zqh.hadoop.nimbus.utils.NimbusOutputStream;
import org.junit.Before;
import org.junit.Test;

public class NimbusInputOutputStreamTest {

	private NimbusOutputStream out = null;
	private NimbusInputStream in = null;
	private PipedOutputStream bytesOut = null;
	private PipedInputStream bytesIn = null;

	@Before
	public void setup() throws IOException {
		bytesOut = new PipedOutputStream();
		bytesIn = new PipedInputStream(bytesOut);
		out = new NimbusOutputStream(bytesOut);
		in = new NimbusInputStream(bytesIn);
	}

	@Test
	public void testNoArgs() throws IOException {

		out.write(0);

		assertEquals(0, in.readCmd());
		assertEquals(0L, in.readNumArgs());
		in.verifyEndOfMessage();
	}

	@Test
	public void testOneEmptyStringArg() throws IOException {

		out.write(0, "");

		assertEquals(0, in.readCmd());
		assertEquals(1L, in.readNumArgs());
		assertEquals("", in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testTwoEmptyStringArgs() throws IOException {

		out.write(0, "", "");

		assertEquals(0, in.readCmd());
		assertEquals(2L, in.readNumArgs());
		assertEquals("", in.readArg());
		assertEquals("", in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testOneNonEmptyStringArg() throws IOException {

		String testStringA = "test string A";
		out.write(0, testStringA);

		assertEquals(0, in.readCmd());
		assertEquals(1L, in.readNumArgs());
		assertEquals(testStringA, in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testTwoNonEmptyStringArgs() throws IOException {

		String testStringA = "test string A";
		String testStringB = "test string B";
		out.write(0, testStringA, testStringB);

		assertEquals(0, in.readCmd());
		assertEquals(2L, in.readNumArgs());
		assertEquals(testStringA, in.readArg());
		assertEquals(testStringB, in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testEmptyCollectionArg() throws IOException {

		ArrayList<String> set = new ArrayList<String>();
		out.write(0, set);

		assertEquals(0, in.readCmd());
		assertEquals(0L, in.readNumArgs());
		in.verifyEndOfMessage();
	}

	@Test
	public void testOneEmptyStringInCollection() throws IOException {

		ArrayList<String> set = new ArrayList<String>();
		set.add("");
		out.write(0, set);

		assertEquals(0, in.readCmd());
		assertEquals(1L, in.readNumArgs());
		assertEquals("", in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testTwoEmptyStringsInCollection() throws IOException {

		ArrayList<String> set = new ArrayList<String>();
		set.add("");
		set.add("");

		out.write(0, set);

		assertEquals(0, in.readCmd());
		assertEquals(2L, in.readNumArgs());
		assertEquals("", in.readArg());
		assertEquals("", in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testOneNonEmptyCollectionArg() throws IOException {

		String testStringA = "test string A";

		ArrayList<String> set = new ArrayList<String>();
		set.add(testStringA);
		out.write(0, set);

		assertEquals(0, in.readCmd());
		assertEquals(1L, in.readNumArgs());
		assertEquals(testStringA, in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testTwoNonEmptyCollectionArgs() throws IOException {

		String testStringA = "test string A";
		String testStringB = "test string B";

		ArrayList<String> set = new ArrayList<String>();
		set.add(testStringA);
		set.add(testStringB);
		out.write(0, set);

		assertEquals(0, in.readCmd());
		assertEquals(2L, in.readNumArgs());
		assertEquals(testStringA, in.readArg());
		assertEquals(testStringB, in.readArg());
		in.verifyEndOfMessage();
	}

	@Test
	public void testStreamingWritesAndReadsNoArgs() throws IOException {

		out.prepStreamingWrite(1, 0);
		out.endStreamingWrite();

		assertEquals(1, in.readCmd());
		assertEquals(0L, in.readNumArgs());

		in.verifyEndOfMessage();
	}

	@Test
	public void testStreamingWritesAndReadsThreeStrings() throws IOException {

		String testStringA = "test string A";
		String testStringB = "";
		String testStringC = "test string C";

		out.prepStreamingWrite(1, 3);

		assertEquals(1, in.readCmd());
		assertEquals(3L, in.readNumArgs());

		out.streamingWrite(testStringA);
		assertEquals(testStringA, in.readArg());

		out.streamingWrite(testStringB);
		assertEquals(testStringB, in.readArg());

		out.streamingWrite(testStringC);
		assertEquals(testStringC, in.readArg());

		out.endStreamingWrite();

		in.verifyEndOfMessage();
	}

	@Test
	public void testStreamingWritesAndReadsThreeStringArrays()
			throws IOException {

		String[] testStringA = new String[] { "test ", "string ", "A" };
		String[] testStringB = new String[] { "", "", "" };
		String[] testStringC = new String[] { "test ", "string ", "C" };

		out.prepStreamingWrite(1, 3 * 3);

		assertEquals(1, in.readCmd());
		assertEquals(9L, in.readNumArgs());

		out.streamingWrite(testStringA);
		assertEquals(testStringA[0], in.readArg());
		assertEquals(testStringA[1], in.readArg());
		assertEquals(testStringA[2], in.readArg());

		out.streamingWrite(testStringB);
		assertEquals(testStringB[0], in.readArg());
		assertEquals(testStringB[1], in.readArg());
		assertEquals(testStringB[2], in.readArg());

		out.streamingWrite(testStringC);
		assertEquals(testStringC[0], in.readArg());
		assertEquals(testStringC[1], in.readArg());
		assertEquals(testStringC[2], in.readArg());

		out.endStreamingWrite();

		in.verifyEndOfMessage();
	}
}
