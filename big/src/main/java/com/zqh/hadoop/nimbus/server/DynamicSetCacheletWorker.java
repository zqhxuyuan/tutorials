package com.zqh.hadoop.nimbus.server;

import java.io.IOException;

import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

import org.apache.log4j.Logger;

public class DynamicSetCacheletWorker extends ICacheletWorker {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(DynamicSetCacheletWorker.class);

	private DynamicSetCacheletServer server = null;

	public DynamicSetCacheletWorker(DynamicSetCacheletServer server) {
		this.server = server;
	}

	@Override
	public void processMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		switch (cmd) {
		case DynamicSetCacheletServer.ISEMPTY_CMD:
			out.write(DynamicSetCacheletServer.ACK_CMD,
					String.valueOf(server.isEmpty()));
			break;
		case DynamicSetCacheletServer.ITER_CMD:
			out.prepStreamingWrite(DynamicSetCacheletServer.ACK_CMD,
					server.size());

			for (String key : server) {
				out.streamingWrite(key);
			}

			out.endStreamingWrite();

			break;
		case DynamicSetCacheletServer.SIZE_CMD:
			out.write(DynamicSetCacheletServer.ACK_CMD,
					String.valueOf(server.size()));
			break;
		case DynamicSetCacheletServer.CLEAR_CMD:
			server.clear();
			break;
		case DynamicSetCacheletServer.CONTAINS_CMD:
			out.write(DynamicSetCacheletServer.ACK_CMD, String.valueOf(server
					.contains(BytesUtil.toString(rdr.readArg()))));
			break;
		case DynamicSetCacheletServer.ADD_CMD:
			out.write(DynamicSetCacheletServer.ACK_CMD, String.valueOf(server
					.add(BytesUtil.toString(rdr.readArg()))));
			break;
		case DynamicSetCacheletServer.ADD_ALL_CMD:
			for (int i = 0; i < numArgs; ++i) {
				server.add(BytesUtil.toString(rdr.readArg()));
			}
			break;
		case DynamicSetCacheletServer.REMOVE_CMD:
			out.write(DynamicSetCacheletServer.ACK_CMD, String.valueOf(server
					.remove(BytesUtil.toString(rdr.readArg()))));
			break;
		default:
			printHelpMessage(cmd, numArgs, rdr);
		}
	}
}
