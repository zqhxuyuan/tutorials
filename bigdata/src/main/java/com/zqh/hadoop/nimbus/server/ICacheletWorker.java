package com.zqh.hadoop.nimbus.server;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.main.NimbusShutdownHook;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;
import com.zqh.hadoop.nimbus.utils.NimbusOutputStream;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

public abstract class ICacheletWorker implements Runnable {

	private final Logger LOG = Logger.getLogger(ICacheletWorker.class);
	protected NimbusInputStream in = null;
	protected NimbusOutputStream out = null;
	protected ICacheletServer server = null;

	/**
	 * Creates an {@link IProtocol} object based on the given {@link CacheType}
	 * to respond to requests.
	 * 
	 * @param type
	 *            The CacheType.
	 * @param socket
	 *            Gets the I/O streams from the socket.
	 * @throws IOException
	 *             If an error occurs when retrieving the streams, or this
	 *             Cachelet does not support the given type.
	 */
	public void initialize(ICacheletServer server, String cacheName,
			String cacheletName, CacheType type, Socket socket)
			throws IOException {
		this.server = server;
		out = new NimbusOutputStream(new DataOutputStream(
				socket.getOutputStream()));
		in = new NimbusInputStream(socket.getInputStream());
	}

	public void setOutputStream(OutputStream outputStream) {
		this.out = new NimbusOutputStream(outputStream);
	}

	/**
	 * Processes requests from the client until the end of the stream is read or
	 * the "kill" command is received.
	 */
	@Override
	public void run() {
		try {
			boolean shutdown = false;
			while (true) {
				try {
					LOG.debug("Waiting for command...");
					int cmd = in.readCmd();
					LOG.debug("Received command " + cmd);
					long numArgs = in.readNumArgs();
					if (cmd == NimbusMaster.KILL_CMD) {
						LOG.info("Kill command received. Deleting Bloom filter from HDFS and exiting...");

						FileSystem.get(NimbusConf.getConf()).delete(
								new Path(Nimbus.CACHELET_ZNODE), false);

						NimbusShutdownHook.getInstance().cleanShutdown();

						shutdown = true;
						break;
					}

					processMessage(cmd, numArgs, in);

					in.verifyEndOfMessage();

				} catch (EOFException e) {
					// ignore this error, the connection was likely closed
					break;
				} catch (Exception e) {
					LOG.error("Caught exception while processing input");
					e.printStackTrace();
					break;
				}
			}

			LOG.info("Closing worker");
			out.close();
			in.close();

			if (shutdown) {
				server.shutdown();
			}
		} catch (SocketException e) {
			e.printStackTrace();
			LOG.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e);
		}
	}

	protected abstract void processMessage(int cmd, long numArgs,
			NimbusInputStream in) throws IOException;

	protected void printHelpMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		for (int i = 0; i < numArgs; ++i) {
			rdr.readArg();
		}

		LOG.error("Received unknown command: " + cmd);
	}
}
