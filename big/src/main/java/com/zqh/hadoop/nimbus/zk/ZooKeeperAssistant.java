package com.zqh.hadoop.nimbus.zk;

import java.io.IOException;
import java.util.List;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.ExceptionType;
import com.zqh.hadoop.nimbus.utils.NimbusException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * ZooKeeperAssistant is intended to be a helper class for setting and getting
 * variables stored in ZooKeeper.<br>
 * <br>
 * It is given a configuration file that stores the root path for the Assistant.<br>
 * <br>
 * Paths given to the variables can be absolute paths based on the root path, or
 * local paths. All local paths will be prepended with the root path before
 * setting any variables, allowing for easier mobility<br>
 * <br>
 * For example, with root path of /nimbus/myconfig, making a call to
 * getIntVariable("myvariable", "100") will get the data from the ZooKeeper node
 * automatically at /nimbus/myconfig/myvariable, convert it to an Integer, and
 * return it to the user. If the path does not exist, then 100 is returned. If
 * the node exists but does not contain a value that can be parsed to an
 * Integer, then 100 is returned as well and an error is logged. Calling
 * getIntVariable("myvariable") to a ZNode that does not exist will return a
 * null Integer. A call to getIntVariable("/nimbus/myconfig/myvariable") will
 * return an identical value.<br>
 * <br>
 * A call to getIntVariable("/nimbus/someotherconfig/myvariable") will result in
 * a NullPointerException, as the Assistant is not set to that configuration.
 * (Specifically, makeAbsolutePath will return null as the given path does not
 * start with "/nimbus/myconfig".)
 */
public class ZooKeeperAssistant implements ConnectListener {

	private static final Logger LOG = Logger
			.getLogger(ZooKeeperAssistant.class);

	private ZooKeeper keeper = null;
	private NimbusConf conf = null;

	private String rootPath = null;
	private byte[] EMPTY_BYTES = new byte[0];
	private boolean connected = false;

	public static Watcher getBlankWatcher() {
		return new NullWatcher();
	}

	public static Watcher getConnectWatcher(ConnectListener listener) {
		return new ConnectWatcher(listener);
	}

	public ZooKeeperAssistant() throws NimbusException {
		this.conf = NimbusConf.getConf();
		this.rootPath = conf.getZooKeeperRootPath();

		LOG.info("Root path is " + this.rootPath);

		try {
			connect();
		} catch (Exception e) {
			LOG.error("Failed to connect to ZooKeeper: " + e.getMessage());
			throw new NimbusException(
					ExceptionType.FAILED_TO_CONNECT_TO_ZOOKEEPER,
					e.getMessage());
		}
	}

	private void connect() {
		connected = false;
		LOG.info("Connecting to ZooKeeper...");
		try {
			keeper = new ZooKeeper(conf.getZooKeeperServers(),
					conf.getZKSessionTimeout(), getConnectWatcher(this));

			while (!connected) {
				Thread.sleep(100);
			}
		} catch (IOException e) {
			LOG.error(e);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			LOG.error(e);
			throw new RuntimeException(e);
		}
	}

	private boolean reconnect(KeeperException ex) {
		if (ex.code().equals(Code.CONNECTIONLOSS)) {
			connected = false;
			LOG.info("Connection lost. Reconnecting to ZooKeeper.");
			try {
				keeper = new ZooKeeper(conf.getZooKeeperServers(),
						conf.getZKSessionTimeout(), getConnectWatcher(this));

				while (!connected) {
					Thread.sleep(100);
				}

				return true;
			} catch (IOException e) {
				LOG.error(e);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
		return false;
	}

	/**
	 * Takes the given path and appends to ZK assistant root path to it, if the
	 * path does not start with a leading '/'. If the given path is null or
	 * empty, returns a null string
	 * 
	 * @param path
	 *            A path to make absolute (could already be absolute)
	 * @return An absolute path, or null
	 */
	public String makeAbsolutePath(String path) {
		String retpath = path;

		if (path == null || path.isEmpty()) {
			LOG.error("Given path is null or empty.");
			return null;
		} else if (path.charAt(0) != '/') {
			retpath = rootPath + path;
		}

		return retpath;
	}

	/**
	 * Gets an ephemeral lock on the given path.
	 * 
	 * Returns true if the path creation was successful, false if the path
	 * already exists. Throws ZKAssistantException If a parent node does not
	 * exist
	 * 
	 * @param path
	 *            The path to get a lock on
	 * @return True if the lock is achieved, false is somebody else has it
	 */
	public synchronized boolean lockPath(String path) {
		try {
			keeper.create(path, EMPTY_BYTES, Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			LOG.info("Created " + path);
			return true;
		} catch (KeeperException e) {
			if (e.code().equals(Code.NODEEXISTS)) {
				return false;
			} else if (e.code().equals(Code.NONODE)) {
				throw new ZKAssistantException(
						"A parent node does not exist for " + path);
			} else {
				LOG.error(e);
				if (reconnect(e)) {
					return lockPath(path);
				} else {
					throw new ZKAssistantException(
							"Unable to reconnect or unsupported error code");
				}
			}
		} catch (InterruptedException e) {
			return lockPath(path);
		}
	}

	public synchronized void ensurePaths(String path) {
		ensurePaths(path, true);
	}

	public synchronized void ensurePaths(String path, CreateMode mode) {
		ensurePaths(path, true, mode);
	}

	public synchronized void ensurePaths(String path, boolean recursive) {
		String absPath = makeAbsolutePath(path);
		if (absPath != null) {
			if (recursive) {
				ensureRecursivePaths(absPath, CreateMode.PERSISTENT);
			} else {
				ensureNonRecursivePath(absPath, CreateMode.PERSISTENT);
			}
		}
	}

	public synchronized void ensurePaths(String path, boolean recursive,
			CreateMode mode) {
		String absPath = makeAbsolutePath(path);
		if (absPath != null) {
			if (recursive) {
				ensureRecursivePaths(absPath, mode);
			} else {
				ensureNonRecursivePath(absPath, mode);
			}
		}
	}

	private synchronized void ensureNonRecursivePath(String absPath,
			CreateMode mode) {
		try {
			keeper.create(absPath, EMPTY_BYTES, Ids.OPEN_ACL_UNSAFE, mode);
			LOG.info("Created " + absPath);
		} catch (KeeperException e) {
			if (e.code().equals(Code.NONODE)) {
				throw new ZKAssistantException(
						"A parent node does not exist for " + absPath);
			} else {
				LOG.error(e);
				if (reconnect(e)) {
					try {
						keeper.create(absPath, EMPTY_BYTES,
								Ids.OPEN_ACL_UNSAFE, mode);
					} catch (KeeperException e1) {
						e1.printStackTrace();
						throw new ZKAssistantException(e1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
						throw new ZKAssistantException(e1);
					}
				} else {
					throw new ZKAssistantException(
							"Unable to reconnect or unsupported error code");
				}
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				keeper.create(absPath, EMPTY_BYTES, Ids.OPEN_ACL_UNSAFE, mode);
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e1);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e1);
			}
		}
	}

	private void ensureRecursivePaths(String absPath, CreateMode mode) {
		String[] paths = absPath.split("/");
		String fullZNode = "";
		for (int i = 1; i < paths.length; ++i) {
			fullZNode += "/" + paths[i];
			try {
				if (keeper.exists(fullZNode, false) == null) {
					keeper.create(fullZNode, EMPTY_BYTES, Ids.OPEN_ACL_UNSAFE,
							mode);
					LOG.info("Created " + fullZNode);
				}
			} catch (KeeperException e) {
				if (e.code().equals(Code.NODEEXISTS)) {
					// ignore -- somebody already created this node
					LOG.info("NODEXISTS exception caught. Ignoring");
				} else {
					LOG.error(e);
					if (reconnect(e)) {
						// try again
						ensureRecursivePaths(absPath, mode);
					} else {
						LOG.error(e);
						throw new ZKAssistantException(
								"Unable to reconnect or unsupported error code");
					}
				}
			} catch (InterruptedException e) {
				LOG.error(e);
				ensureRecursivePaths(absPath, mode);
			}
		}
	}

	public synchronized boolean exists(String path) {
		String absPath = makeAbsolutePath(path);

		try {
			if (absPath != null) {
				LOG.info("Checking exists for " + absPath + " value\t "
						+ (keeper.exists(absPath, false) != null));
				return keeper.exists(absPath, false) != null;
			} else {
				throw new ZKAssistantException(
						"Given path does not start with " + path
								+ ".  Returning null.");
			}
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					return keeper.exists(absPath, false) != null;
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				return keeper.exists(absPath, false) != null;
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}
	}

	public synchronized void deletePaths(String path, boolean recursive) {
		String absPath = makeAbsolutePath(path);

		try {
			if (keeper.exists(absPath, false) != null) {
				int numChildren;
				try {
					numChildren = keeper.getChildren(absPath, false).size();
				} catch (KeeperException e) {
					LOG.error(e);
					if (reconnect(e)) {
						try {
							numChildren = keeper.getChildren(absPath, false)
									.size();
						} catch (KeeperException e1) {
							e1.printStackTrace();
							throw new ZKAssistantException(e);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
							throw new ZKAssistantException(e);
						}
					} else {
						throw new ZKAssistantException(
								"Unable to reconnect or unsupported error code");
					}
				} catch (InterruptedException e) {
					LOG.error(e);
					try {
						numChildren = keeper.getChildren(absPath, false).size();
					} catch (KeeperException e1) {
						throw new ZKAssistantException(e);
					} catch (InterruptedException e1) {
						throw new ZKAssistantException(e);
					}
				}

				if (recursive) {
					if (numChildren != 0) {
						deletePathsHelper(absPath);
					} else {
						try {
							keeper.delete(absPath, -1);
						} catch (KeeperException e) {
							LOG.error(e);
							if (reconnect(e)) {
								try {
									keeper.delete(absPath, -1);
								} catch (KeeperException e1) {
									e1.printStackTrace();
									throw new ZKAssistantException(e);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
									throw new ZKAssistantException(e);
								}
							} else {
								throw new ZKAssistantException(
										"Unable to reconnect or unsupported error code");
							}
						} catch (InterruptedException e) {
							LOG.error(e);
							try {
								keeper.delete(absPath, -1);
							} catch (KeeperException e1) {
								throw new ZKAssistantException(e);
							} catch (InterruptedException e1) {
								throw new ZKAssistantException(e);
							}
						}
					}
				} else if (numChildren != 0) {
					throw new ZKAssistantException("Node " + absPath
							+ " has children.  Must do recursive delete.");
				} else {
					try {
						keeper.delete(absPath, -1);
					} catch (KeeperException e) {
						LOG.error(e);
						if (reconnect(e)) {
							try {
								keeper.delete(absPath, -1);
							} catch (KeeperException e1) {
								e1.printStackTrace();
								throw new ZKAssistantException(e);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
								throw new ZKAssistantException(e);
							}
						} else {
							throw new ZKAssistantException(
									"Unable to reconnect or unsupported error code");
						}
					} catch (InterruptedException e) {
						LOG.error(e);
						try {
							keeper.delete(absPath, -1);
						} catch (KeeperException e1) {
							throw new ZKAssistantException(e);
						} catch (InterruptedException e1) {
							throw new ZKAssistantException(e);
						}
					}
				}
			}
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				deletePaths(path, recursive);
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			deletePaths(path, recursive);
		}
	}

	private void deletePathsHelper(String path) {

		List<String> children;
		try {
			children = keeper.getChildren(path, false);
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					children = keeper.getChildren(path, false);
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				children = keeper.getChildren(path, false);
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}

		for (String child : children) {
			deletePathsHelper(path + "/" + child);
		}

		try {
			keeper.delete(path, -1);
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					keeper.delete(path, -1);
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				keeper.delete(path, -1);
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}
	}

	public Boolean getBooleanVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			return Boolean.parseBoolean(var);
		}
	}

	public Boolean getBooleanVariable(String path, boolean def) {
		String var = getStringVariable(path, Boolean.toString(def));

		if (var == null) {
			return null;
		} else {
			return Boolean.parseBoolean(var);
		}
	}

	public boolean setBooleanVariable(String path, boolean b) {
		return setStringVariable(path, Boolean.toString(b));
	}

	public Character getCharVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			return Character.valueOf(var.charAt(0));
		}
	}

	public Character getCharVariable(String path, char def) {
		String var = getStringVariable(path, Character.toString(def));

		if (var == null || var.length() == 0) {
			return null;
		} else {
			return var.charAt(0);
		}
	}

	public boolean setCharVariable(String path, char c) {
		return setStringVariable(path, Character.toString(c));
	}

	public byte[] getDataVariable(String path) {
		return getDataVariable(path, null, null);
	}

	public byte[] getDataVariable(String path, byte[] def) {
		return getDataVariable(path, null, def);
	}

	public synchronized byte[] getDataVariable(String path, Watcher watch,
			byte[] def) {
		byte[] data = null;
		try {
			if (watch != null) {
				data = keeper.getData(makeAbsolutePath(path), watch, null);
			} else {
				data = keeper.getData(makeAbsolutePath(path), false, null);
			}
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					if (watch != null) {
						data = keeper.getData(makeAbsolutePath(path), watch,
								null);
					} else {
						data = keeper.getData(makeAbsolutePath(path), false,
								null);
					}
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				if (watch != null) {
					data = keeper.getData(makeAbsolutePath(path), watch, null);
				} else {
					data = keeper.getData(makeAbsolutePath(path), false, null);
				}
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}

		if (data != null) {
			return data;
		} else {
			return def;
		}
	}

	public synchronized boolean setDataVariable(String path, byte[] data) {
		LOG.trace("Setting " + path + " to " + data.length + " bytes.");
		String absPath = makeAbsolutePath(path);

		if (absPath == null) {
			LOG.error("Given value " + path
					+ " does not form to this Assistant's configuration");
			throw new ZKAssistantException("Given value " + path
					+ " does not form to this Assistant's configuration");
		}

		try {
			keeper.setData(absPath, data, -1);
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					keeper.setData(absPath, data, -1);
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				keeper.setData(absPath, data, -1);
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}

		return true;
	}

	public Double getDoubleVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			return Double.parseDouble(var);
		}
	}

	public Double getDoubleVariable(String path, double def) {
		String var = getStringVariable(path, Double.toString(def));

		if (var == null) {
			return null;
		} else {
			try {
				return Double.parseDouble(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return def;
			}
		}
	}

	public boolean setDoubleVariable(String path, double d) {
		return setStringVariable(path, Double.toString(d));
	}

	public Float getFloatVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			return Float.parseFloat(var);
		}
	}

	public Float getFloatVariable(String path, float def) {
		String var = getStringVariable(path, Float.toString(def));

		if (var == null) {
			return null;
		} else {
			try {
				return Float.parseFloat(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return def;
			}
		}
	}

	public boolean setFloatVariable(String path, float f) {
		return setStringVariable(path, Float.toString(f));
	}

	public Integer getIntVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			try {
				return Integer.parseInt(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return null;
			}
		}
	}

	public Integer getIntVariable(String path, int def) {
		String var = getStringVariable(path, Integer.toString(def));

		if (var == null) {
			return null;
		} else {
			try {
				return Integer.parseInt(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return def;
			}
		}
	}

	public boolean setIntVariable(String path, int i) {
		return setStringVariable(path, Integer.toString(i));
	}

	public Long getLongVariable(String path) {
		String var = getStringVariable(path, null);

		if (var == null) {
			return null;
		} else {
			try {
				return Long.parseLong(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return null;
			}
		}
	}

	public Long getLongVariable(String path, long def) {
		String var = getStringVariable(path, Long.toString(def));

		if (var == null) {
			return null;
		} else {
			try {
				return Long.parseLong(var);
			} catch (NumberFormatException e) {
				LOG.error("Failed to format number correctly: "
						+ e.getMessage() + ".  Returning default.");
				return def;
			}
		}
	}

	public boolean setLongVariable(String path, long i) {
		return setStringVariable(path, Long.toString(i));
	}

	public String getStringVariable(String path) {
		return getStringVariable(path, null);
	}

	public String getStringVariable(String path, String def) {

		byte[] data = def != null ? getDataVariable(path,
				BytesUtil.toBytes(def)) : getDataVariable(path);

		if (data != null) {
			return new String(data);
		} else {
			return def;
		}
	}

	public boolean setStringVariable(String path, String str) {
		return setDataVariable(path, BytesUtil.toBytes(str));
	}

	public synchronized List<String> getChildren(String path) {
		return getChildren(path, null);
	}

	public synchronized List<String> getChildren(String path, Watcher watch) {
		String absPath = makeAbsolutePath(path);

		if (absPath == null) {
			LOG.error("Given value " + path
					+ " does not form to this Assistant's configuration");
			throw new ZKAssistantException("Given value " + path
					+ " does not form to this Assistant's configuration");
		}

		try {
			if (watch != null) {
				return keeper.getChildren(absPath, watch);
			} else {
				return keeper.getChildren(absPath, false);
			}
		} catch (KeeperException e) {
			LOG.error(e);
			if (reconnect(e)) {
				try {
					if (watch != null) {
						return keeper.getChildren(absPath, watch);
					} else {
						return keeper.getChildren(absPath, false);
					}
				} catch (KeeperException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					throw new ZKAssistantException(e);
				}
			} else {
				throw new ZKAssistantException(
						"Unable to reconnect or unsupported error code");
			}
		} catch (InterruptedException e) {
			LOG.error(e);
			try {
				if (watch != null) {
					return keeper.getChildren(absPath, watch);
				} else {
					return keeper.getChildren(absPath, false);
				}
			} catch (KeeperException e1) {
				throw new ZKAssistantException(e);
			} catch (InterruptedException e1) {
				throw new ZKAssistantException(e);
			}
		}
	}

	public ZooKeeper getZooKeeper() {
		return keeper;
	}

	public synchronized void close() {
		if (keeper != null) {
			try {
				keeper.close();
			} catch (InterruptedException e) {
				LOG.error(e);
				keeper = null;
			}
		}
	}

	@Override
	public void connected() throws IOException {
		connected = true;
		LOG.info("Connected to ZooKeeper.");
	}

	@Override
	public void closing() throws IOException {
		connected = false;

		try {
			LOG.error("Session expired from ZooKeeper... Attempting reconnect");
			keeper = new ZooKeeper(conf.getZooKeeperServers(),
					conf.getZKSessionTimeout(), getConnectWatcher(this));

			while (!connected) {
				Thread.sleep(100);
			}
		} catch (IOException e) {
			LOG.error("Failed to connect to ZooKeeper: " + e.getMessage());
			throw new IOException("Failed to connect to ZooKeeper: "
					+ e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("Failed to connect to ZooKeeper: "
					+ e.getMessage());
		}
	}
}
