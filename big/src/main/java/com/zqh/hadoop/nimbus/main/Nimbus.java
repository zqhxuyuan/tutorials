package com.zqh.hadoop.nimbus.main;

import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zqh.hadoop.nimbus.client.BaseNimbusClient;
import com.zqh.hadoop.nimbus.master.CacheInfo;
import com.zqh.hadoop.nimbus.server.DynamicSetCacheletServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.master.NimbusSafetyNet;
import com.zqh.hadoop.nimbus.server.DynamicMapCacheletServer;
import com.zqh.hadoop.nimbus.server.ICacheletServer;
import com.zqh.hadoop.nimbus.server.CacheType;
import com.zqh.hadoop.nimbus.server.MapSetCacheletServer;
import com.zqh.hadoop.nimbus.server.StaticSetCacheletServer;
import com.zqh.hadoop.nimbus.server.MasterCacheletServer;
import com.zqh.hadoop.nimbus.utils.BigBitArray;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.NimbusException;
import com.zqh.hadoop.nimbus.zk.ZooKeeperAssistant;

/**
 * This is the main class for starting up a Cache. It parses command line
 * arguments and opens up a server based on the command line arguments to
 * receive requests from clients. The main class itself is responsible for
 * establishing connections between Cachelets themselves, as well as sending a
 * heartbeat to ZooKeeper.
 * 
 * See the PDF file located under $NIMBUS_HOME/doc for more information about
 * Nimbus, including overall design and features.
 */
public class Nimbus extends Configured implements Tool {

	public static final String ROOT_ZNODE = "/com/zqh/hadoop/nimbus";
	public static String CACHE_ZNODE = null;
	public static String CACHELET_ZNODE = null;

	private static final Logger LOG = Logger.getLogger(Nimbus.class);
	private static CacheInfo info = null;
	private static Random rndm = new Random();
	private static ZooKeeperAssistant s_zk = null;

	private static String cacheName;
	private static int port;
	private static CacheType type;

	// Options
	private Options options = null;
	private CommandLineParser parser = new PosixParser();
	private CommandLine line;
	private Map<String, BaseNimbusClient> knownServers = new HashMap<String, BaseNimbusClient>();
	private ICacheletServer cachelet;

	@Override
	public int run(String[] args) throws Exception {

		parseOptions(args);

		if (line.hasOption("start")) {
			startCache();
		} else if (line.hasOption("kill")) {
			NimbusMaster.getInstance().destroy(line.getOptionValue("kill"));
		} else {
			throw new InvalidParameterException("Unknown mode to run in.");
		}

		return 0;
	}

	private void startCache() throws Exception {

		LOG.info("Starting Nimbus cache");
		Nimbus.getZooKeeper();
		LOG.info("ZooKeeper finished");

		// Set the Cache ZNode to the root + the Cache name
		CACHE_ZNODE = ROOT_ZNODE + "/" + cacheName;
		String cacheletName = InetAddress.getLocalHost().getHostName();
		CACHELET_ZNODE = CACHE_ZNODE + "/" + cacheletName;
		knownServers.put(InetAddress.getLocalHost().getHostName(), null);

		// add shutdown hook
		NimbusShutdownHook.createInstance(type);
		LOG.info("making root node");
		// ensure this root path exists
		getZooKeeper().ensurePaths(ROOT_ZNODE);

		LOG.info("done making root node");
		if (type.equals(CacheType.MASTER)) {
			createMasterCacheInfo();
		} else {
			// this info is for a non-master cache
			info = NimbusMaster.getInstance().getCacheInfo(cacheName);
			if (info == null) {
				throw new RuntimeException("No info for " + cacheName);
			}
		}

		// create my Cachelet
		switch (info.getType()) {
		case STATIC_SET:
			cachelet = new StaticSetCacheletServer(info.getName(),
					cacheletName, info.getPort(), info.getType());
			break;
		case DYNAMIC_SET:
			cachelet = new DynamicSetCacheletServer(info.getName(),
					cacheletName, info.getPort(), info.getType());
			break;
		case MASTER:
			cachelet = new MasterCacheletServer(info.getName(), cacheletName,
					info.getPort(), info.getType());
			break;
		case MAPSET:
			cachelet = new MapSetCacheletServer(info.getName(), cacheletName,
					info.getPort(), info.getType());
			break;
			
		case DYNAMIC_MAP:
			cachelet = new DynamicMapCacheletServer(info.getName(), cacheletName,
					info.getPort(), info.getType());
			break;
		default:
			LOG.error("Unkown type " + info.getType().toString()
					+ ". Shutting down");
			System.exit(0);
		}

		Thread t = new Thread(cachelet);
		t.start();

		// add myself to ZooKeeper
		LOG.info("Creating my ZNode at " + CACHELET_ZNODE);
		getZooKeeper().ensurePaths(CACHELET_ZNODE);
		
		if (NimbusConf.getConf().isSafetyNetEnabled()) {
			// this while loop manages connections to other Cachelets
			// if a Cachelet connects, then create a new thread to handle
			// communication to that Cachelet and wait for more connections
			LOG.info("Starting heartbeat cycle...");
			long hbInterval = NimbusConf.getConf()
					.getCacheletHeartbeatInterval();
			getZooKeeper().setDataVariable(CACHELET_ZNODE,
					BytesUtil.EMPTY_BYTES);
			while (!false) {
				Thread.sleep(hbInterval);
				getZooKeeper().setDataVariable(CACHELET_ZNODE,
						BytesUtil.EMPTY_BYTES);
			}
		} else {
			LOG.info("Safety net is disabled... simply sleeping this thread.");
			while (!false) {
				Thread.sleep(Integer.MAX_VALUE);
			}
		}
	}

	private void createMasterCacheInfo() {

		LOG.info("creating master cache info");
		// check if the Cache node exists, if it doesn't create it
		NimbusMaster.getInstance().getCacheInfoLock(cacheName);
		info = NimbusMaster.getInstance().getCacheInfo(cacheName);
		if (info == null) {
			LOG.info("CacheInfo is null.  Creating CacheZNode...");
			info = new CacheInfo();
			info.setName(cacheName);
			info.setType(type);
			info.setPort(port);

			BigBitArray array = new BigBitArray(
					BigBitArray.makeMultipleOfEight(NimbusConf.getConf()
							.getNumNimbusCachelets()));
			info.setAvailabilityArray(array.getBytes());

			getZooKeeper().ensurePaths(CACHE_ZNODE);
			getZooKeeper().setDataVariable(CACHE_ZNODE,
					info.getByteRepresentation());

			LOG.info("Creating Cache ZNode at " + CACHE_ZNODE
					+ " with data of size "
					+ info.getByteRepresentation().length);
		} else {
			LOG.info("CacheInfo is not null");
		}

		NimbusMaster.getInstance().releaseCacheInfoLock(cacheName);

		if (NimbusConf.getConf().isSafetyNetEnabled()) {
			// Fire up the Safety Net
			Thread safetynet = new Thread(NimbusSafetyNet.getInstance());
			safetynet.start();
		}
	}

	/**
	 * Returns a random Nimbus Server machine address.
	 * 
	 * @return A random Nimbus Server machine address.
	 */
	public static String getRandomNimbusHost() {
		String[] hosts = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		return hosts[Math.abs(rndm.nextInt()) % hosts.length];
	}

	/**
	 * Returns the ZooKeeper instance.
	 * 
	 * @return The ZooKeeper instance.
	 */
	public static ZooKeeperAssistant getZooKeeper() {
		if (s_zk == null) {
			// Create ZK Instance
			try {
				LOG.info("Connecting to ZooKeeper at "
						+ NimbusConf.getConf().getZooKeeperServers());
				s_zk = new ZooKeeperAssistant();

			} catch (NimbusException e) {
				e.printStackTrace();
				LOG.error("Failed to initialize ZooKeeper");
				System.exit(-1);
			}
		}

		return s_zk;
	}

	public static void main(String[] args) {
		try {
			System.exit(ToolRunner.run(new Configuration(), new Nimbus(), args));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// ///////////
	// OPTIONS //
	// ///////////

	@SuppressWarnings("static-access")
	private Options getOptions() {
		if (options == null) {
			options = new Options();

			options.addOption(OptionBuilder.withLongOpt("start")
					.withDescription("Start a cache").create('s'));

			options.addOption(OptionBuilder.withLongOpt("kill").hasArg()
					.withDescription("Kill a cache").create('s'));

			options.addOption(OptionBuilder.withLongOpt("port").hasArg()
					.withDescription("Port to initialize Nimbus with.")
					.create('p'));
			options.addOption(OptionBuilder.withLongOpt("name").hasArg()
					.withDescription("Name of this Cache.").create('n'));
			options.addOption(OptionBuilder.withLongOpt("type").hasArg()
					.withDescription("Cache type.").create('t'));

			options.addOption(OptionBuilder.withLongOpt("help")
					.withDescription("Displays this help message.").create());
		}
		return options;
	}

	private void parseOptions(String[] args) {

		if (args.length == 0) {
			printHelp();
			System.exit(0);
		}

		try {
			line = parser.parse(getOptions(), args);

			if (line.hasOption("start") && line.hasOption("kill")) {
				throw new ParseException(
						"Cannot simultaneously start and kill a cache");
			}

			// verify all required options are there
			if (line.hasOption("start")
					&& (!line.hasOption("port") || !line.hasOption("name") || !line
							.hasOption("type"))) {
				throw new ParseException(
						"Cannot start cache without port, name, and type params");
			}
		} catch (MissingOptionException e) {
			System.err.println(e.getMessage());
			printHelp();
			System.exit(-1);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		if (line.hasOption("help")) {
			printHelp();
			System.exit(0);
		}

		if (line.hasOption("start")) {
			cacheName = line.getOptionValue("name");
			type = CacheType.valueOf(line.getOptionValue("type").toUpperCase());
			port = Integer.parseInt(line.getOptionValue("port"));

			if (type == null) {
				LOG.error("Invalid type.");
				printHelp();
				System.exit(-1);
			}
		}
	}

	private void printHelp() {
		HelpFormatter help = new HelpFormatter();
		help.printHelp("java -jar $NIMBUS_HOME/bin/nimbus.jar [opts]",
				"Command Line Arguments", getOptions(), "");
	}
}
