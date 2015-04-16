package com.zqh.hadoop.nimbus.server;

import java.io.IOException;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.nativestructs.CSet;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

import org.apache.log4j.Logger;

public class MapSetCacheletWorker extends ICacheletWorker {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(MapSetCacheletWorker.class);

	public static final int CONTAINS_KEY_CMD = 1;
	public static final int CONTAINS_KEY_VALUE_CMD = 2;
	public static final int ISEMPTY_CMD = 3;
	public static final int ADD_CMD = 4;
	public static final int REMOVE_KEY_CMD = 5;
	public static final int REMOVE_KEY_VALUE_CMD = 6;
	public static final int CLEAR_CMD = 7;
	public static final int SIZE_CMD = 8;
	public static final int GET_CMD = 9;
	public static final int GET_ALL_CMD = 10;
	public static final int ACK_CMD = 11;

	private MapSetCacheletServer server = null;

	public MapSetCacheletWorker(MapSetCacheletServer server) {
		this.server = server;
	}

	@Override
	public void processMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		switch (cmd) {
		case REMOVE_KEY_VALUE_CMD:
			server.remove(BytesUtil.toString(rdr.readArg()),
					BytesUtil.toString(rdr.readArg()));
			break;
		case CONTAINS_KEY_VALUE_CMD:
			out.write(
					ACK_CMD,
					String.valueOf(server.contains(
							BytesUtil.toString(rdr.readArg()),
							BytesUtil.toString(rdr.readArg()))));
			break;
		case ADD_CMD:
			server.add(BytesUtil.toString(rdr.readArg()),
					BytesUtil.toString(rdr.readArg()));
			break;
		case ISEMPTY_CMD:
			out.write(ACK_CMD, String.valueOf(server.isEmpty()));
			break;
		case CLEAR_CMD:
			server.clear();
			break;
		case SIZE_CMD:
			out.write(ACK_CMD, String.valueOf(server.size()));
			break;
		case GET_ALL_CMD:
			// synchronize on server instance (this), have a thread to push out
			// all the values
			out.prepStreamingWrite(ACK_CMD, server.size());
			String[] kv = new String[2];
			for (Entry<String, CSet> entry : server) {
				kv[0] = entry.getKey();
				for (String value : entry.getValue()) {
					kv[1] = value;
					out.streamingWrite(kv);
				}
			}
			break;
		case CONTAINS_KEY_CMD:
			out.write(ACK_CMD, String.valueOf(server.contains(BytesUtil
					.toString(rdr.readArg()))));
			break;
		case REMOVE_KEY_CMD:
			server.remove(BytesUtil.toString(rdr.readArg()));
			break;
		case GET_CMD:
			CSet set = server.get(BytesUtil.toString(rdr.readArg()));
			if (set != null) {
				out.prepStreamingWrite(ACK_CMD, set.size());
				for (String value : set) {
					out.streamingWrite(value);
				}
			} else {
				out.prepStreamingWrite(ACK_CMD, 0);
			}

			out.endStreamingWrite();

			break;
		default:
			printHelpMessage(cmd, numArgs, rdr);
		}
	}
}
