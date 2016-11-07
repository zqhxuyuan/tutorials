package com.zqh.hadoop.nimbus.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zqh.hadoop.nimbus.client.BaseNimbusClient;
import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

import com.zqh.hadoop.nimbus.server.CacheType;
import com.zqh.hadoop.nimbus.utils.BigBitArray;

/**
 * The NimbusMaster is a Singleton object that allows access for creating and
 * destroying Caches by the Master service. The Master service accepts client
 * requests to create and destroy Caches.<br>
 * <br>
 * <b>Note that this class itself should not be used by applications to create
 * and destroy Caches.</b> Instead, utilize the {@link MasterClient} to connect
 * to the Master service.<br>
 * <br>
 * The eventual intent is to deprecate the {@link MasterClient} and have
 * applications simply use this class.<br>
 * <br>
 * The NimbusMaster relies heavily on ZooKeeper to deliver and retrieve
 * {@link CacheInfo}, as it does not store the information itself.
 */
public class NimbusMaster implements ISafetyNetListener {

	public static final int KILL_CMD = 0;
	
	private static final Logger LOG = Logger.getLogger(NimbusMaster.class);
	private List<Integer> ports = new ArrayList<Integer>();
	private static NimbusMaster s_instance;
	private static final String CACHE_INFO_LOCK = "/nimbus-master-info-lock";
	private static final String CACHE_RESTART_LOCK = "/nimbus-master-restart-lock-";

	private NimbusMaster() {
		// Get the ports Nimbus can use from the configuration
		String[] strPorts = NimbusConf.getConf().getNimbusCacheletPortRange()
				.split(",");
		for (String s : strPorts) {
			if (s.contains("-")) {
				for (int i = Integer.parseInt(s.split("-")[0]); i <= Integer
						.parseInt(s.split("-")[1]); ++i) {
					ports.add(i);
				}
			} else {
				ports.add(Integer.parseInt(s));
			}
		}
	}

	/**
	 * Gets the Singleton instance of the NimbusMaster. Creates the instance if
	 * it does not already exist.
	 * 
	 * @return The NimbusMaster
	 */
	public static NimbusMaster getInstance() {
		if (s_instance == null) {
			s_instance = new NimbusMaster();
			NimbusSafetyNet.getInstance().addListener(s_instance);
		}

		return s_instance;
	}

	/**
	 * Communicates with ZooKeeper to determine if a Cache exists.
	 * 
	 * @param name
	 *            The Cache to check for.
	 * @throws RuntimeException
	 *             If any other ZooKeeper related error occurs.
	 * @return If the Cache exists.
	 */
	public boolean exists(String name) {
		return Nimbus.getZooKeeper().exists(name);
	}

	/**
	 * The CacheInfo lock is meant for users who want to modify the CacheInfo
	 * stored in ZooKeeper in a safe way. If you don't intend to modify the
	 * CacheInfo, then there is no need to obtain a lock.<br>
	 * <br>
	 * <b>Note</b> that this method call blocks until the CacheInfo lock is
	 * released.
	 */
	public void getCacheInfoLock(String cacheName) {
		boolean locked = false;
		do {
			LOG.info("Getting lock for " + cacheName);

			while (!Nimbus.getZooKeeper().lockPath(
					CACHE_INFO_LOCK + "-" + cacheName)) {
				LOG.info("Somebody already has it... waiting");

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}
			}

			locked = true;

			LOG.info("Got lock for Cache " + cacheName);

		} while (!locked);
	}

	/**
	 * Releases the CacheInfo lock.
	 */
	public void releaseCacheInfoLock(String cacheName) {
		if (Nimbus.getZooKeeper().exists(CACHE_INFO_LOCK + "-" + cacheName)) {
			Nimbus.getZooKeeper().deletePaths(
					CACHE_INFO_LOCK + "-" + cacheName, false);
			LOG.info("Released lock for Cache " + cacheName);
		}
	}

	/**
	 * Retrieves the Cache information from ZooKeeper.
	 * 
	 * @param name
	 *            The name of the Cache.
	 * @return The Cache info or null if the Cache does not exist.
	 * @throws RuntimeException
	 *             If any other ZooKeeper related error occurs.
	 */
	public CacheInfo getCacheInfo(String name) {
		if (Nimbus.getZooKeeper().exists(name)) {
			try {
				return new CacheInfo(Nimbus.getZooKeeper().getDataVariable(
						Nimbus.ROOT_ZNODE + "/" + name));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Stores the given {@link CacheInfo} in ZooKeeper for that Cache.
	 * 
	 * @param name
	 * @param info
	 *            The Cache information to set.
	 * @return True if the operation was successful, false if the Cache does not
	 *         exist.
	 */
	public boolean setCacheInfo(String name, CacheInfo info) {
		if (Nimbus.getZooKeeper().exists(name)) {

			Nimbus.getZooKeeper().setDataVariable(name,
					info.getByteRepresentation());

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper function to retrieve a Cache port from the Master service. This
	 * method assumes you are already connected to the Master service. It is a
	 * little clunky and will most likely no longer exist in the near future.
	 * 
	 * @param cacheName
	 *            The Cache to retrieve a port form.
	 * @return The port for the Cache
	 * @throws CacheDoesNotExistException
	 *             If the Cache does not exist.
	 */
	public int getCachePort(String name) throws CacheDoesNotExistException {
		CacheInfo info = getCacheInfo(name);
		if (info == null) {
			throw new CacheDoesNotExistException(name);
		} else {
			return info.getPort();
		}
	}

	/**
	 * Used by the Master service to create a Cache. Selects a random port based
	 * on the range from {@link NimbusConf#getNimbusCacheletPortRange()}. <br>
	 * <br>
	 * <b>Applications should not call this function explicitly. Use the
	 * {@link MasterClient} to create/destroy Caches.</b>
	 * 
	 * @param name
	 *            The Cache to create.
	 * @param type
	 *            The type of Cache to create.
	 * @return True if the operation was successful,
	 * @throws FailedToCreateCacheException
	 *             If an error occurs when creating the Cache, such as the Cache
	 *             already existing.
	 */
	public void create(String name, CacheType type)
			throws FailedToCreateCacheException {
		if (exists(name)) {
			throw new FailedToCreateCacheException(name);
		}

		LOG.info("Cache " + name + " does not exist. Creating...");
		int port;
		Random rndm = new Random();
		do {
			port = ports.get(Math.abs(rndm.nextInt()) % ports.size());
			if (isPortAvailable(port)) {
				break;
			}
		} while (!false);

		CacheInfo info = new CacheInfo();
		info.setName(name);
		info.setType(type);
		info.setPort(port);

		BigBitArray array = new BigBitArray(
				BigBitArray.makeMultipleOfEight(NimbusConf.getConf()
						.getNumNimbusCachelets()));
		info.setAvailabilityArray(array.getBytes());

		try {

			Nimbus.getZooKeeper().ensurePaths(name);
			Nimbus.getZooKeeper().setDataVariable(name,
					info.getByteRepresentation());

			LOG.info("Creating Cache ZNode at " + Nimbus.CACHE_ZNODE
					+ " with data of size "
					+ info.getByteRepresentation().length);
		} catch (Exception e) {
			throw new FailedToCreateCacheException(e.getMessage());
		}

		List<String> cmds = new ArrayList<String>();
		cmds.add(NimbusConf.getConf().getNimbusHomeDir() + "/bin/start.sh");
		cmds.add(name);
		cmds.add(Integer.toString(port));
		cmds.add(type.toString());

		ProcessBuilder p = new ProcessBuilder();
		p.command(cmds);

		String cmd = "";
		for (String s : cmds) {
			cmd += s + " ";
		}

		LOG.info("Starting process " + cmd);

		String opts = NimbusConf.getConf().getJavaOpts();
		if (opts != null && opts.length() != 0) {
			p.environment().put("NIMBUS_JAVA_OPTS", opts);
		}

		Process proc = null;
		try {
			proc = p.start();
			proc.waitFor();

			if (proc.exitValue() == 0) {
				LOG.info("Successfully created Cache " + name + " on port "
						+ port);
				printProcessLogs(proc);
			} else {
				LOG.error("Failed to create Cache " + name);
				printProcessLogs(proc);
				throw new FailedToCreateCacheException(name);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new FailedToCreateCacheException(name);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			throw new FailedToCreateCacheException(name);
		}
	}

	/**
	 * Used by the Master service to destroy a Cache. Connects to each Cachelet
	 * and tells it to shutdown. Also deletes any Bloom filters for the Cache.<br>
	 * <br>
	 * <b>Applications should not call this function explicitly. Use the
	 * {@link MasterClient} to destroy Caches.</b>
	 * 
	 * @param name
	 *            The Cache to destroy.
	 * @return True if the Cache is destroyed, false if there is an error.
	 */
	public boolean destroy(String name) {

		try {
			int port = getCachePort(name);

			for (String host : getCacheletNames(name)) {
				LOG.info("Killing Cache " + name + " on machine " + host);
				BaseNimbusClient client = new BaseNimbusClient(host, port);
				client.connect();
				client.write(NimbusMaster.KILL_CMD);
				client.disconnect();
			}
			return true;
		} catch (CacheDoesNotExistException e) {
			LOG.error(e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			return false;
		}
	}

	/**
	 * Retrieves a unique Cachelet ID based on the name.
	 * 
	 * @param cacheletName
	 *            The name of the Cachelet.
	 * @return The unique Cachelet ID, or -1 if the Cachelet does not exist.
	 */
	public int getCacheletID(String cacheletName) {
		String[] cachelets = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		for (int i = 0; i < cachelets.length; ++i) {
			if (cachelets[i].equals(cacheletName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets the given Cachelet's availability in ZooKeeper. This method
	 * inherently calls {@link NimbusMaster#getCacheInfoLock()} and releases the
	 * lock. Because of this, deadlocks may occur if users attempt to set
	 * Cachelet availability while still holding a Cache info lock.
	 * 
	 * @param cacheName
	 * @param cacheletName
	 * @param available
	 * @return
	 */
	public boolean setCacheletAvailability(String cacheName,
			String cacheletName, boolean available) {

		NimbusMaster.getInstance().getCacheInfoLock(cacheName);
		CacheInfo info = getCacheInfo(cacheName);
		boolean retval = false;
		if (info != null) {
			BigBitArray array = new BigBitArray(info.getAvailabilityArray());
			int id = getCacheletID(cacheletName);
			if (array.isBitOn(id) != available) {
				LOG.info("Setting Cachelet " + cacheName + "/" + cacheletName
						+ " availability to " + available);
				array.set(id, available);
				info.setAvailabilityArray(array.getBytes());
				setCacheInfo(cacheName, info);
			}
			retval = true;
		}
		NimbusMaster.getInstance().releaseCacheInfoLock(cacheName);
		return retval;
	}

	@Override
	public void onCacheAdded(String name) {
	}

	@Override
	public void onCacheRemoved(String name) {
	}

	@Override
	public void onCacheletAdded(String cacheName, String cacheletName) {
	}

	@Override
	public void onCacheletRemoved(String cacheName, String cacheletName) {
	}

	@Override
	public void onCacheletStale(String cacheName, String cacheletName) {
		String lockname = CACHE_RESTART_LOCK + cacheName + "-" + cacheletName;

		if (getRestartLock(lockname)) {
			LOG.info("Got lock on " + lockname + ".  Cachelet " + cacheletName
					+ " has gone stale.  Restarting...");
		} else {
			LOG.info("Someone else has lock on " + lockname);
			return;
		}

		setCacheletAvailability(cacheName, cacheletName, false);

		CacheInfo info = NimbusMaster.getInstance().getCacheInfo(cacheName);
		if (info != null) {
			restartProcess(info, cacheletName);
		}

		releaseRestartLock(lockname);

		LOG.info("Finalized restart process.");
	}

	/**
	 * Gets the current list of host machines for the Cache.
	 * 
	 * @param cacheName
	 *            The Cache to retrieve the host machines from.
	 * @return A list of host names or null if the Cache does not exist.
	 * @throws RuntimeException
	 *             If a ZooKeeper related error occurs.
	 */
	private List<String> getCacheletNames(String cacheName) {
		return Nimbus.getZooKeeper().getChildren(cacheName);
	}

	/**
	 * Returns a Boolean value as to whether or not a port is available. A port
	 * is available if a {@link ServerSocket} can be successfully opened and
	 * closed without an {@link IOException}.
	 * 
	 * @param port
	 *            The port to check availability.
	 * @return If the port is available.
	 */
	private static boolean isPortAvailable(int port) {
		try {
			ServerSocket srv = new ServerSocket(port);
			srv.close();
			srv = null;
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Checks ZooKeeper to attempt to get a lock for restarting a service. The
	 * Master that gets there first, gets to restart a Cachelet.
	 * 
	 * @param lockname
	 *            The lock to get.
	 * @return Whether or not this Cachelet has gotten the lock.
	 */
	private boolean getRestartLock(String lockname) {
		if (!Nimbus.getZooKeeper().exists(lockname)) {
			Nimbus.getZooKeeper().ensurePaths(lockname, CreateMode.EPHEMERAL);
			return true;
		}
		return false;
	}

	/**
	 * Releases the given lock in ZooKeeper.
	 * 
	 * @param lockname
	 *            The lock to release.
	 */
	private void releaseRestartLock(String lockname) {
		if (Nimbus.getZooKeeper().exists(lockname)) {
			Nimbus.getZooKeeper().deletePaths(lockname, false);
		}
	}

	/**
	 * Restarts a given Cachelet of a particular Cache. SSHs to the node and
	 * fires up the Cachelet service.
	 * 
	 * @param info
	 *            The Cache info related to this Cachelet.
	 * @param cacheletName
	 *            The name of the Cachelet to restart.
	 * @return Whether or not the operation was successful.
	 */
	private boolean restartProcess(CacheInfo info, String cacheletName) {

		List<String> cmds = new ArrayList<String>();
		cmds.add(NimbusConf.getConf().getNimbusHomeDir() + "/bin/start-one.sh");
		cmds.add(info.getName());
		cmds.add(cacheletName);
		cmds.add(Integer.toString(info.getPort()));
		cmds.add(info.getType().toString());

		ProcessBuilder p = new ProcessBuilder();

		p.command(cmds);
		String cmd = "";
		for (String s : cmds) {
			cmd += s + " ";
		}

		LOG.info("Starting process " + cmd);
		p.environment().put("NIMBUS_HOME",
				NimbusConf.getConf().getNimbusHomeDir());
		p.environment().put("JAVA_HOME", NimbusConf.getConf().getJavaHomeDir());
		String opts = NimbusConf.getConf().getJavaOpts();
		if (opts != null && opts.length() != 0) {
			p.environment().put("NIMBUS_JAVA_OPTS", opts);
		}

		try {
			Process proc = p.start();
			proc.waitFor();

			if (proc.exitValue() == 0) {
				LOG.info("Successfully restarted Cachelet " + cacheletName
						+ " for Cache " + info.getName());
				return true;
			} else {
				LOG.error("Failed to create Cachelet " + cacheletName);
				printProcessLogs(proc);
				return false;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			return false;
		}
	}

	/**
	 * Logs the stdout and stderr streams from the process.
	 * 
	 * @param proc
	 *            The process to log the messages.
	 */
	private void printProcessLogs(Process proc) {
		try {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));

			LOG.info("Printing stdout");
			String s;
			while ((s = rdr.readLine()) != null) {
				LOG.info(s);
			}
			rdr.close();

			rdr = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));

			LOG.info("Printing stderr");
			while ((s = rdr.readLine()) != null) {
				LOG.info(s);
			}

			rdr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
