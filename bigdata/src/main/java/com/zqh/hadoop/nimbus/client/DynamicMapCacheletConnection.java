package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.nativestructs.CMapEntry;
import com.zqh.hadoop.nimbus.server.DynamicMapCacheletServer;

public class DynamicMapCacheletConnection extends BaseNimbusClient implements
		Iterable<Entry<String, String>> {

	public DynamicMapCacheletConnection(String cacheName, String cacheletName)
			throws CacheDoesNotExistException, IOException {
		super(cacheletName, NimbusMaster.getInstance().getCachePort(cacheName));
		super.cacheName = cacheName;
		connect();
	}

	public Iterator<Entry<String, String>> iterator() {
		return new DynamicMapCacheletIterator();
	}

	public String put(String key, String value) throws IOException {

		super.write(DynamicMapCacheletServer.PUT_CMD, key, value);

		String response = null;
		if (super.in.readCmd() == DynamicMapCacheletServer.ACK_CMD) {
			super.in.readNumArgs();
			response = BytesUtil.toString(super.in.readArg());
		} else {
			super.in.readNumArgs();
		}

		in.verifyEndOfMessage();

		return response;
	}

	public void putAll(Map<? extends String, ? extends String> values)
			throws IOException {
		super.write(DynamicMapCacheletServer.PUT_ALL_CMD, values);
	}

	public void clear() throws IOException {
		super.write(DynamicMapCacheletServer.CLEAR_CMD);
	}

	public boolean containsKey(String key) throws IOException,
			CacheletNotConnectedException {
		super.write(DynamicMapCacheletServer.CONTAINS_KEY_CMD, key);

		if (super.in.readCmd() != DynamicMapCacheletServer.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());

		in.verifyEndOfMessage();

		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		} else {
			throw new IOException("Did not receive a true or false response.");
		}
	}

	public boolean containsValue(String value) throws IOException,
			CacheletNotConnectedException {
		super.write(DynamicMapCacheletServer.CONTAINS_VALUE_CMD, value);

		if (super.in.readCmd() != DynamicMapCacheletServer.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());

		in.verifyEndOfMessage();

		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		} else {
			throw new IOException("Did not receive a true or false response.");
		}
	}

	public String get(String key) throws IOException {

		super.write(DynamicMapCacheletServer.GET_CMD, key);

		String response = null;
		if (super.in.readCmd() == DynamicMapCacheletServer.ACK_CMD) {
			super.in.readNumArgs();
			response = BytesUtil.toString(super.in.readArg());
		} else {
			super.in.readNumArgs();
		}

		in.verifyEndOfMessage();

		return response;
	}

	public boolean isEmpty() throws IOException {
		super.write(DynamicMapCacheletServer.ISEMPTY_CMD);

		if (super.in.readCmd() != DynamicMapCacheletServer.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());

		in.verifyEndOfMessage();

		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		} else {
			throw new IOException("Did not receive a true or false response.");
		}
	}

	public String remove(String key) throws IOException {
		super.write(DynamicMapCacheletServer.REMOVE_CMD, key);

		String response = null;
		if (super.in.readCmd() == DynamicMapCacheletServer.ACK_CMD) {
			super.in.readNumArgs();
			response = BytesUtil.toString(super.in.readArg());
		} else {
			super.in.readNumArgs();
		}

		in.verifyEndOfMessage();

		return response;
	}

	public int size() throws IOException {
		super.write(DynamicMapCacheletServer.SIZE_CMD);

		if (super.in.readCmd() != DynamicMapCacheletServer.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		int retval = Integer.valueOf(BytesUtil.toString(super.in.readArg()));

		in.verifyEndOfMessage();
		return retval;
	}

	public class DynamicMapCacheletIterator implements
			Iterator<Entry<String, String>> {

		private long numEntries = 0, entriesRead = 0;
		private final Logger LOG = Logger
				.getLogger(DynamicMapCacheletIterator.class);
		private Entry<String, String> currEntry = new CMapEntry();

		public DynamicMapCacheletIterator() {
			try {
				write(DynamicMapCacheletServer.ITER_CMD);
				if (in.readCmd() != DynamicMapCacheletServer.ACK_CMD) {
					throw new IOException("Did not receive ACK_CMD");
				}

				numEntries = in.readNumArgs();
				LOG.info("Need to read " + numEntries);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean hasNext() {
			if (entriesRead == numEntries) {
				try {
					in.verifyEndOfMessage();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			return entriesRead < numEntries;
		}

		@Override
		public Entry<String, String> next() {
			if (entriesRead >= numEntries) {
				return null;
			} else {
				++entriesRead;
				try {
					currEntry = new CMapEntry(BytesUtil.toString(in.readArg()),
							BytesUtil.toString(in.readArg()));
					return currEntry;
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException(
					"NimbusSetCacheletIterator::remove is unsupported");
		}

		public float getProgress() {
			return (float) entriesRead / (float) numEntries;
		}
	}
}