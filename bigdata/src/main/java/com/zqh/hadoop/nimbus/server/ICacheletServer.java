package com.zqh.hadoop.nimbus.server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;
import com.zqh.hadoop.nimbus.utils.WriteAheadFile;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils.NullOutputStream;
import org.apache.log4j.Logger;

/**
 * A Cachelet opens up a server on the given port and creates an appropriate
 * {@link IProtocol} based on the given {@link CacheType}. <br>
 * <br>
 * A Cachelet is responsible for creating threads to handle each client
 * connection, as well as holding the object that represents (such as a
 * {@link com.zqh.hadoop.nimbus.nativestructs.CSet}). <br>
 * <br>
 * This class contains a private class to actually respond to messages sent by
 * clients.
 */
public abstract class ICacheletServer implements Runnable {

	private static final Logger LOG = Logger.getLogger(ICacheletServer.class);

	protected List<ICacheletWorker> workers = new ArrayList<ICacheletWorker>();

	protected int port = 0;
	protected CacheType type = null;
	protected String cacheName = null;
	protected String cacheletName = null;
	private WriteAheadFile waffle = null;

	private ServerSocket serverSocket;

	/**
	 * Initializes a new instance of the {@link ICacheletServer} class. <br>
	 * <br>
	 * Does not actually create the server until {@link ICacheletServer#run()}
	 * is called.
	 * 
	 * @param port
	 *            The port to create the server on.
	 * @param type
	 *            The type of Cache to create.
	 */
	public ICacheletServer(String cacheName, String cacheletName, int port,
			CacheType type) {
		this.port = port;
		this.type = type;
		this.cacheName = cacheName;
		this.cacheletName = cacheletName;
	}

	/**
	 * Opens up the server and creates a thread for each connection to it.
	 */
	@Override
	public void run() {
		openServer();

		newWriteAheadFile();

		try {
			recover();
			waffle.open();
			
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}


		startStatusThread();

		acceptConnections();
	}

	protected abstract ICacheletWorker getNewWorker();

	protected abstract void startStatusThread();

	protected void recover() throws IOException {

		Path[] files = NimbusConf.getConf().getPastWriteAheadLogs(cacheName,
				cacheletName);

		for (Path log : files) {
			ICacheletWorker worker = getNewWorker();

			worker.setOutputStream(new NullOutputStream());

			LOG.info("Recovering from " + log);

			NimbusInputStream in = new NimbusInputStream(FileSystem.get(
					NimbusConf.getConf()).open(log));

			waffle.setOutputStream(new NullOutputStream());

			int numCommands = 0, cmd = 0;
			long numArgs = 0;
			try {
				while ((cmd = in.readCmd()) != NimbusInputStream.EOF) {
					numArgs = in.readNumArgs();

					worker.processMessage(cmd, numArgs, in);

					in.verifyEndOfMessage();
					++numCommands;
				}
			} catch (EOFException e) {
				in.close();
				LOG.info("Recovered.  Processed " + numCommands + " commands");
			} catch (IOException e) {
				LOG.info("IOException on recover.  Read " + numCommands
						+ " commands.", e);
				throw e;
			}
		}
	}

	protected void openServer() {
		LOG.info("Opening up server on port " + port);
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			LOG.error("Could not listen on port " + port);
			System.exit(1);
		}
	}

	protected void acceptConnections() {
		while (true) {
			ICacheletWorker w = null;
			try {
				w = getNewWorker();
				w.initialize(this, cacheName, cacheletName, type,
						serverSocket.accept());

				Thread t = new Thread(w);
				t.start();
				LOG.info("Started a new worker");
			} catch (IOException e) {
				LOG.error("Accept failed: " + e.getMessage());
				System.exit(1);
			}
		}
	}

	public WriteAheadFile getWriteAheadFile() {
		return waffle;
	}

	public void shutdown() {
		try {
			if (waffle != null) {
				LOG.info("Closing write ahead file");
				waffle.flush();
				waffle.close();
			} else {
				LOG.info("Write ahead file is null.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOG.error("Shutting down");
		System.exit(0);
	}

	public void newWriteAheadFile() {
		try {

			if (waffle != null) {
				LOG.info("Closing write ahead file");
				waffle.flush();
				waffle.close();
			}

			waffle = new WriteAheadFile(NimbusConf.getConf().getWriteAheadLog(
					cacheName, cacheletName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}