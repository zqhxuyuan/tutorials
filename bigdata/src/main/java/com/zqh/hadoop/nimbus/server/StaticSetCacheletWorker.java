package com.zqh.hadoop.nimbus.server;

import java.io.IOException;

import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

import org.apache.log4j.Logger;

public class StaticSetCacheletWorker extends ICacheletWorker {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(StaticSetCacheletWorker.class);

	/**
	 * The CONTAINS command will determine if a given element is a member of
	 * this Cachelet's set.
	 */
	public static final int CONTAINS_CMD = 1;

	/**
	 * The ISEMPTY command will return a value based on if this Cachelet is
	 * empty or not.
	 */
	public static final int ISEMPTY_CMD = 2;

	/**
	 * The GET command will stream all values back to the user
	 */
	public static final int GET_CMD = 3;

	public static final int ACK_CMD = 4;

	private StaticSetCacheletServer server = null;

	public StaticSetCacheletWorker(StaticSetCacheletServer server) {
		this.server = server;
	}

	@Override
	protected void processMessage(int cmd, long numArgs, NimbusInputStream in)
			throws IOException {
	
		switch (cmd) {
		case ISEMPTY_CMD:
			out.write(ACK_CMD, String.valueOf(server.isEmpty()));
			break;
		case GET_CMD:
			out.prepStreamingWrite(ACK_CMD, server.size());
			for (String key : server) {
				out.streamingWrite(BytesUtil.toBytes(key));
			}
			out.endStreamingWrite();

			break;
		case CONTAINS_CMD:
			out.write(ACK_CMD,
					String.valueOf(server.contains(BytesUtil.toString(in.readArg()))));
			break;
		default:
			printHelpMessage(cmd, numArgs, in);
			break;
		}
	}
}
