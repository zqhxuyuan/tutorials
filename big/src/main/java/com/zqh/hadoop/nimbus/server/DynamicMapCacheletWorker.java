package com.zqh.hadoop.nimbus.server;

import java.io.IOException;

import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

import org.apache.log4j.Logger;

public class DynamicMapCacheletWorker extends ICacheletWorker {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(DynamicMapCacheletWorker.class);

	private DynamicMapCacheletServer server = null;

	public DynamicMapCacheletWorker(DynamicMapCacheletServer server) {
		this.server = server;
	}

	@Override
	public void processMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		switch (cmd) {
		case DynamicMapCacheletServer.ISEMPTY_CMD:
			out.write(DynamicMapCacheletServer.ACK_CMD, String.valueOf(server.isEmpty()));
			break;
		case DynamicMapCacheletServer.GET_CMD:
			String oldValue = server.get(BytesUtil.toString(rdr.readArg()));
			if (oldValue != null) {
				out.write(DynamicMapCacheletServer.ACK_CMD, oldValue);
			} else {
				out.write(DynamicMapCacheletServer.DNE_CMD);
			}

			break;
		case DynamicMapCacheletServer.SIZE_CMD:
			out.write(DynamicMapCacheletServer.ACK_CMD, String.valueOf(server.size()));
			break;
		case DynamicMapCacheletServer.CLEAR_CMD:
			server.clear();
			break;			
		case DynamicMapCacheletServer.ITER_CMD:
						
			out.prepStreamingWrite(DynamicMapCacheletServer.ACK_CMD, server.size());

			for (Entry<String, String> entry : server) {
				out.streamingWrite(entry.getKey());
				out.streamingWrite(entry.getValue());
			}

			out.endStreamingWrite();
			
			
			break;
		case DynamicMapCacheletServer.CONTAINS_KEY_CMD:
			out.write(DynamicMapCacheletServer.ACK_CMD, String.valueOf(server.containsKey(BytesUtil
					.toString(rdr.readArg()))));
			break;
		case DynamicMapCacheletServer.CONTAINS_VALUE_CMD:
			out.write(DynamicMapCacheletServer.ACK_CMD, String.valueOf(server.containsValue(BytesUtil
					.toString(rdr.readArg()))));
			break;
		case DynamicMapCacheletServer.PUT_CMD:
			oldValue = server.put(BytesUtil.toString(rdr.readArg()),
					BytesUtil.toString(rdr.readArg()));
			if (oldValue != null) {
				out.write(DynamicMapCacheletServer.ACK_CMD, oldValue);
			} else {
				out.write(DynamicMapCacheletServer.DNE_CMD);
			}
			break;
		case DynamicMapCacheletServer.PUT_ALL_CMD:
			for (int i = 0; i < numArgs; ++i) {
				server.put(BytesUtil.toString(rdr.readArg()),
						BytesUtil.toString(rdr.readArg()));
			}
			break;
		case DynamicMapCacheletServer.REMOVE_CMD:
			out.write(DynamicMapCacheletServer.ACK_CMD, server.remove(BytesUtil.toString(rdr.readArg())));
			break;
		default:
			printHelpMessage(cmd, numArgs, rdr);
		}
	}
}
