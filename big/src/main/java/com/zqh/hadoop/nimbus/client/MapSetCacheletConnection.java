package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.server.MapSetCacheletWorker;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import org.apache.log4j.Logger;

public class MapSetCacheletConnection extends BaseNimbusClient implements
		Iterable<Entry<String, String>> {

	private static final Logger LOG = Logger
			.getLogger(MapSetCacheletConnection.class);

	/**
	 * Connects to the given host. Automatically gets the port from the Master
	 * based on the given Cache name.
	 * 
	 * @param cacheName
	 *            The Cache to connect to.
	 * @param cacheletName
	 *            The host of the Cachelet.
	 * @throws com.zqh.hadoop.nimbus.master.CacheDoesNotExistException
	 *             If the Cache does not exist.
	 * @throws IOException
	 *             If some bad juju happens.
	 */
	public MapSetCacheletConnection(String cacheName, String cacheletName)
			throws CacheDoesNotExistException, IOException {
		super(cacheletName, NimbusMaster.getInstance().getCachePort(cacheName));
		connect();
		LOG.info("Connected to " + cacheletName);
	}

	public void add(String key, String value) throws IOException {
		super.write(MapSetCacheletWorker.ADD_CMD, key, value);
	}

	public void remove(String key) throws IOException {
		super.write(MapSetCacheletWorker.REMOVE_KEY_CMD, key);
	}

	public void remove(String key, String value) throws IOException {
		super.write(MapSetCacheletWorker.REMOVE_KEY_VALUE_CMD, key, value);
	}

	public boolean contains(String key) throws IOException {
		super.write(MapSetCacheletWorker.CONTAINS_KEY_CMD, key);

		if (super.in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());
		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		}

		throw new IOException("Did not receive a true or false response.");
	}

	public boolean contains(String key, String value) throws IOException {
		super.write(MapSetCacheletWorker.CONTAINS_KEY_VALUE_CMD, key, value);

		if (super.in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
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

	public boolean isEmpty() throws IOException {
		super.write(MapSetCacheletWorker.ISEMPTY_CMD);

		if (super.in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
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

	public void clear() throws IOException {
		super.write(MapSetCacheletWorker.CLEAR_CMD);
	}

	public long size() throws IOException {

		super.write(MapSetCacheletWorker.SIZE_CMD);

		if (super.in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		long retval = Long.valueOf(BytesUtil.toString(super.in.readArg()));
		in.verifyEndOfMessage();
		return retval;
	}

	public Iterator<String> get(String key) throws IOException {
		return new SetCacheletIterator(key);
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		try {
			return new MapSetCacheletIterator();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public class SetCacheletIterator implements Iterator<String> {

		private long size, read = 0;

		public SetCacheletIterator(String key) throws IOException {
			write(MapSetCacheletWorker.GET_CMD, key);
			if (in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
				throw new IOException("Did not receive ACK_CMD");
			}

			size = in.readNumArgs();
		}

		public float getProgress() {
			return (float) read / (float) size;
		}

		@Override
		public boolean hasNext() {
			if (read == size) {
				try {
					in.verifyEndOfMessage();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				;
			}
			return read < size;
		}

		@Override
		public String next() {

			try {
				if (read >= size) {
					return null;
				}

				++read;
				return BytesUtil.toString(in.readArg());
			} catch (IOException e) {
				e.printStackTrace();
				read = size;
				return null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"MapSetCacheletIterator::remove is not supported");
		}
	}

	public class MapSetCacheletIterator implements
			Iterator<Entry<String, String>> {

		private long size, read = 0;

		public MapSetCacheletIterator() throws IOException {
			write(MapSetCacheletWorker.GET_ALL_CMD);

			if (in.readCmd() != MapSetCacheletWorker.ACK_CMD) {
				throw new IOException("Did not receive ACK_CMD");
			}

			size = in.readNumArgs();
		}

		public float getProgress() {
			return (float) read / (float) size;
		}

		@Override
		public boolean hasNext() {
			if (read == size) {
				try {
					in.verifyEndOfMessage();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				;
			}
			return read < size;
		}

		@Override
		public Entry<String, String> next() {

			try {
				if (read >= size) {
					return null;
				}

				++read;
				return new MapSetEntry(BytesUtil.toString(in.readArg()), BytesUtil.toString(
						in.readArg()));
			} catch (IOException e) {
				e.printStackTrace();
				read = size;
				return null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"MapSetCacheletIterator::remove is not supported");
		}
	}
}