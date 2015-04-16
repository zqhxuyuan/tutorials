package com.zqh.hadoop.nimbus.server;

import java.io.IOException;

import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

public class MasterCacheletWorker extends ICacheletWorker {

	private final Logger LOG = Logger.getLogger(MasterCacheletWorker.class);

	public static final int DESTROY_CMD = 1;
	public static final int CREATE_CMD = 2;
	public static final int ACK_CMD = 3;

	@Override
	public void processMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		switch (cmd) {
		case CREATE_CMD:
			try {

				NimbusMaster.getInstance().create(
						BytesUtil.toString(rdr.readArg()),
						CacheType.valueOf(BytesUtil.toString(rdr.readArg())
								.toUpperCase()));
				out.write(ACK_CMD, BytesUtil.TRUE_BYTES);
			} catch (IOException e) {
				LOG.error(e.getMessage());
				out.write(ACK_CMD, BytesUtil.FALSE_BYTES);
			}
			break;
		case DESTROY_CMD:
			out.write(
					ACK_CMD,
					String.valueOf(NimbusMaster.getInstance().destroy(
							BytesUtil.toString(rdr.readArg()))));
			break;
		default:
			printHelpMessage(cmd, numArgs, rdr);
			break;
		}
	}
}