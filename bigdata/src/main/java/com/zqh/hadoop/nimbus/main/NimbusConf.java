package com.zqh.hadoop.nimbus.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.utils.CacheletHashType;

/**
 * This class is designed to handle all Nimbus configuration parameters. The
 * default file is loaded based off the $NIMBUS_HOME environment variable, and
 * is located at $NIMBUS_HOME/conf/nimbus-default.xml.<br>
 * <br>
 * 
 * It follows a Singleton paradigm and has helper functions to retrieve
 * configuration values.
 */
public class NimbusConf extends Configuration {

	private static final Logger LOG = Logger.getLogger(NimbusConf.class);

	public static final String NIMBUS_HOME_CONF_VAR = "nimbus.home";
	public static final String NIMBUS_MASTER_PORT_CONF_VAR = "nimbus.master.port";
	public static final String NIMBUS_PORTS_VAR = "nimbus.ports";
	public static final String NIMBUS_SERVER_CONF_VAR = "nimbus.servers";
	public static final String JAVA_HOME_CONF_VAR = "java.home";
	public static final String ZK_SERVERS_CONF_VAR = "zookeeper.quorum.servers";
	public static final String NIMBUS_NUM_SERVERS_CONF_VAR = "nimbus.num.servers";
	public static final String NIMBUS_JAVA_OPTS = "nimbus.java.opts";
	public static final String SERVER_HASH_TYPE = "nimbus.server.hash";
	public static final String NIMBUS_SAFETY_NET_TIMEOUT = "nimbus.safety.net.timeout";
	public static final String NIMBUS_SAFETY_NET_ENABLED = "nimbus.safety.net.enabled";
	public static final String NIMBUS_CACHELET_HEARTBEAT = "nimbus.cachelet.heartbeat";
	public static final String NIMBUS_REPLICATION_FACTOR = "nimbus.replication.factor";
	public static final String WRITE_AHEAD_LOG_DIR = "write.ahead.log.dir";
	public static final String ZK_ASSISTANT_ROOT_PATH = "zk.assistant.root.path";
	public static final String ZK_SESSION_TIMEOUT = "zk.session.timeout";

	private static NimbusConf s_instance = null;

	// private static final Configuration conf = new Configuration();

	public static NimbusConf getConf() {
		synchronized (NimbusConf.class) {
			if (s_instance == null) {
				LOG.info("Instance is null.  Loading configuration");
				loadConfiguration();

				if (s_instance.isSafetyNetEnabled()
						&& s_instance.getCacheletHeartbeatInterval() >= s_instance
								.getSafetyNetTimeout()) {
					throw new RuntimeException(
							"Error: Cachelet heartbeat interval is greater than the safety net timeout.");
				}
			}
		}
		return s_instance;
	}

	private NimbusConf() {
	}

	public Configuration getConfiguration() {
		return s_instance;
	}

	public String getNimbusHomeDir() {
		return s_instance.get(NIMBUS_HOME_CONF_VAR);
	}

	public String getNimbusMasterPort() {
		return s_instance.get(NIMBUS_MASTER_PORT_CONF_VAR);
	}

	public String getNimbusCacheletPortRange() {
		return s_instance.get(NIMBUS_PORTS_VAR);
	}

	public String getNimbusCacheletAddresses() {
		return s_instance.get(NIMBUS_SERVER_CONF_VAR);
	}

	public String getJavaHomeDir() {
		String home = s_instance.get(JAVA_HOME_CONF_VAR);
		if (home == null) {
			throw new RuntimeException(
					"java.home property not set in $NIMBUS_HOME/s_instance/nimbus-default.xml");
		}
		return home;
	}

	public int getNumNimbusCachelets() {
		return Integer.parseInt(s_instance.get(NIMBUS_NUM_SERVERS_CONF_VAR));
	}

	public String getZooKeeperServers() {
		return s_instance.get(ZK_SERVERS_CONF_VAR);
	}

	public String getJavaOpts() {
		return s_instance.get(NIMBUS_JAVA_OPTS);
	}

	public CacheletHashType getCacheletHashType() {
		return CacheletHashType.valueOf(s_instance.get(SERVER_HASH_TYPE));
	}

	public long getSafetyNetTimeout() {
		return Long.parseLong(s_instance.get(NIMBUS_SAFETY_NET_TIMEOUT));
	}

	public long getCacheletHeartbeatInterval() {
		return Long.parseLong(s_instance.get(NIMBUS_CACHELET_HEARTBEAT));
	}

	public boolean isSafetyNetEnabled() {
		return Boolean.parseBoolean(s_instance.get(NIMBUS_SAFETY_NET_ENABLED));
	}

	public int getReplicationFactor() {
		return Integer.parseInt(s_instance.get(NIMBUS_REPLICATION_FACTOR));
	}

	private static void loadConfiguration() {
		try {

			if (System.getenv("NIMBUS_HOME") == null) {
				throw new RuntimeException(
						"NIMBUS_HOME environment variable not set");
			}

			if (System.getenv("HADOOP_HOME") == null) {
				throw new RuntimeException(
						"HADOOP_HOME environment variable not set");
			}

			LOG.info("Creating base configuration");
			s_instance = new NimbusConf();

			loadResource(System.getenv().get("HADOOP_HOME")
					+ "/conf/core-site.xml");
			loadResource(System.getenv().get("HADOOP_HOME")
					+ "/conf/mapred-site.xml");
			loadResource(System.getenv().get("HADOOP_HOME")
					+ "/conf/hdfs-site.xml");

			loadResource(System.getenv().get("NIMBUS_HOME")
					+ "/conf/nimbus-default.xml");
			loadResource(System.getenv().get("NIMBUS_HOME")
					+ "/conf/nimbus-site.xml");

			String servers = "";
			BufferedReader rdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(System.getenv("NIMBUS_HOME")
							+ "/conf/servers")));

			String tmp;
			int numcachelets = 0;
			while ((tmp = rdr.readLine()) != null) {
				if (!tmp.startsWith("#")) {
					servers += tmp + ",";
					++numcachelets;
				}
			}

			rdr.close();

			s_instance.set(NIMBUS_SERVER_CONF_VAR,
					servers.substring(0, servers.length() - 1));
			s_instance.setInt(NIMBUS_NUM_SERVERS_CONF_VAR, numcachelets);
		} catch (FileNotFoundException e) {
			LOG.error("Failed to find configuration file.");
			LOG.error(e.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			System.exit(-1);
		}
	}

	private static void loadResource(String resource) {
		File confFile = new File(resource);
		if (!confFile.exists()) {
			throw new RuntimeException(confFile.toString() + " not found");
		} else {
			LOG.info("Adding " + resource + " to nimbus conf");
			s_instance.addResource(new Path(confFile.toString()));
		}
	}

	public String getZooKeeperRootPath() {
		return s_instance.get(ZK_ASSISTANT_ROOT_PATH)
				+ (s_instance.get(ZK_ASSISTANT_ROOT_PATH).endsWith("/") ? ""
						: "/");
	}

	public int getZKSessionTimeout() {
		return Integer.parseInt(s_instance.get(ZK_SESSION_TIMEOUT));
	}

	public Path getWriteAheadLogDir() {
		return new Path(s_instance.get(WRITE_AHEAD_LOG_DIR));
	}

	public Path getWriteAheadLog(String cacheName, String cacheletName) {
		return new Path(s_instance.get(WRITE_AHEAD_LOG_DIR) + "/" + cacheName
				+ "/" + cacheletName + "/" + System.currentTimeMillis());
	}

	public Path[] getPastWriteAheadLogs(String cacheName, String cacheletName)
			throws IOException {

		FileStatus[] files = FileSystem.get(this).globStatus(
				new Path(s_instance.get(WRITE_AHEAD_LOG_DIR) + "/" + cacheName
						+ "/" + cacheletName + "/*"));
		
		Path[] retval = new Path[files.length];

		for (int i = 0; i < files.length; ++i) {
			retval[i] = files[i].getPath();
		}

		Arrays.sort(retval);

		return retval;
	}
}
