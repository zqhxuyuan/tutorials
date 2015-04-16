package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import com.zqh.hadoop.nimbus.utils.DataZNodeWatcher;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;
import com.zqh.hadoop.nimbus.utils.NimbusOutputStream;

/**
 * This is the base class for all clients in Nimbus. It handles connecting to a
 * Cachelet on a given host and port.<br>
 * <br>
 * It has helpful methods for broadcasting.retrieving messages to/from the
 * Cachelet.
 */
public class BaseNimbusClient implements Runnable {

	protected static FileSystem fs = null;
	private static final Logger LOG = Logger.getLogger(BaseNimbusClient.class);
	protected DataZNodeWatcher watcher = new DataZNodeWatcher();

	static {
		try {
			fs = FileSystem.get(NimbusConf.getConf());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected NimbusOutputStream out = null;
	protected NimbusInputStream in = null;

	protected String host = null;
	protected int port = 0;
	protected Socket socket = null;
	protected String cacheName;
	protected boolean connected = false;

	/**
	 * Sets the client to bind to the given host and port. Doesn't actually
	 * connect until {@link BaseNimbusClient#connect()}is called.
	 * 
	 * @param host
	 *            The machine address.
	 * @param port
	 *            The port to connect to.
	 */
	public BaseNimbusClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Closes the stream just in case the user didn't.
	 */
	@Override
	protected void finalize() throws Throwable {
		disconnect();
		super.finalize();
	}

	/**
	 * Threaded functionality of the client. Used by Nimbus to handle
	 * communications between Cachelets. Users of Nimbus should call
	 * {@link BaseNimbusClient#connect()}.
	 */
	@Override
	public void run() {
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Connects this client to the host and port given during initialization.
	 * Sets the out and in protected member variables to the streams on the
	 * socket.
	 */
	public void connect() throws IOException {
		if (connected) {
			return;
		}

		long sleep = 1000;
		int failattempts = 10;
		while (!connected) {
			try {
				socket = new Socket(host, port);
				connected = true;
				LOG.info("Connected to " + host + " on port " + port);
			} catch (ConnectException e) {
				--failattempts;
				if (failattempts != 0) {
					LOG.error("Failed to connect to " + host + " on port "
							+ port + ".  Sleeping for "
							+ (sleep * (10 - failattempts))
							+ " ms and retrying " + failattempts
							+ " more times...");
					try {
						Thread.sleep(sleep * (10 - failattempts));
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} else {
					throw new IOException("Failed to connect to " + host
							+ " too many times.");
				}
			}
		}

		out = new NimbusOutputStream(socket.getOutputStream());
		in = new NimbusInputStream(socket.getInputStream());

		LOG.debug("Connected to " + host + " on port " + port);
	}

	/**
	 * Reads from the input stream until EOF is reached.
	 */
	public void dumpResponses() throws IOException {
		in.skipToEOF();
	}

	/**
	 * Closes the socket and I/O streams.
	 */
	public void disconnect() throws IOException {
		connected = false;
		if (out != null) {
			out.close();
			out = null;
		}
		if (in != null) {
			in.close();
			in = null;
		}
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void write(int cmd) throws IOException {
		if (connected) {
			out.write(cmd);
		} else {
			throw new CacheletNotConnectedException(host);
		}
	}

	public void write(int cmd, String... args) throws IOException,
			CacheletNotConnectedException {
		if (connected) {
			out.write(cmd, args);
		} else {
			throw new CacheletNotConnectedException(host);
		}
	}

	public void write(int cmd, byte[]... args) throws IOException,
			CacheletNotConnectedException {
		if (connected) {
			out.write(cmd, args);
		} else {
			throw new CacheletNotConnectedException(host);
		}
	}

	/**
	 * Writes the given Collection of String to the output stream, appending a
	 * newline character to each string
	 * 
	 * @param args
	 *            The strings to broadcast
	 * @throws IOException
	 *             If an error occurs when sending the message.
	 */
	public void write(int cmd, Collection<? extends String> args)
			throws IOException {
		if (connected) {
			out.write(cmd, args);
		} else {
			throw new CacheletNotConnectedException(host);
		}
	}

	public void write(int cmd, Map<? extends String, ? extends String> values)
			throws IOException {
		if (connected) {
			out.write(cmd, values);
		} else {
			throw new CacheletNotConnectedException(host);
		}
	}

	/**
	 * Gets a value indicating if this client is currently connected.
	 * 
	 * @return Whether or not this client is currently connected.
	 */
	public boolean isConnected() {
		return connected;
	}
}