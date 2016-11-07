package com.zqh.hadoop.nimbus.server;

import java.io.IOException;
import java.util.Iterator;

import com.zqh.hadoop.nimbus.nativestructs.Triple;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.utils.NimbusInputStream;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

public class TripleSetCacheletWorker extends ICacheletWorker {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(TripleSetCacheletWorker.class);

	/**
	 * The DIS_READ command will ingest a file into HDFS and keep members based
	 * on the given Cachelet ID.
	 */
	public static final int DIS_READ_CMD = 3;

	/**
	 * The CONTAINS command will determine if a given element is a member of
	 * this Cachelet's set.
	 */
	public static final int CONTAINS_CMD = 4;

	/**
	 * The ISEMPTY command will return a value based on if this Cachelet is
	 * empty or not.
	 */
	public static final int ISEMPTY_CMD = 5;

	/**
	 * The ADD command returns a Boolean value is the Cachelet successfully
	 * added the given Triple
	 */
	public static final int ADD_CMD = 6;

	public static final int GET_ALL_CMD = 7;
	public static final int GET_WITH_ONE_CMD = 8;
	public static final int GET_WITH_TWO_CMD = 9;

	public static final int ACK_CMD = 10;

	private TripleSetCacheletServer server = null;

	public TripleSetCacheletWorker(TripleSetCacheletServer server) {
		this.server = server;
	}

	@Override
	public void processMessage(int cmd, long numArgs, NimbusInputStream rdr)
			throws IOException {

		switch (cmd) {
		case CONTAINS_CMD:
			out.write(
					ACK_CMD,
					String.valueOf(server.contains(
							BytesUtil.toString(rdr.readArg()),
							BytesUtil.toString(rdr.readArg()),
							BytesUtil.toString(rdr.readArg()))));
			break;
		case ADD_CMD:
			out.write(ACK_CMD, String.valueOf(server.add(
					BytesUtil.toString(rdr.readArg()),
					BytesUtil.toString(rdr.readArg()),
					BytesUtil.toString(rdr.readArg()))));
			break;
		case ISEMPTY_CMD:
			out.write(ACK_CMD, String.valueOf(server.isEmpty()));
			break;
		case GET_ALL_CMD:
			out.prepStreamingWrite(ACK_CMD, server.size() * 3);
			Iterator<Triple> iter = server.iterator();
			while (iter.hasNext()) {
				out.streamingWrite(iter.next().getTripleArray());
			}

			break;
		case GET_WITH_ONE_CMD:

			String s1 = BytesUtil.toString(rdr.readArg());
			out.prepStreamingWrite(ACK_CMD, server.sizeOf(s1) * 3);
			iter = server.iterator(s1);
			while (iter.hasNext()) {
				out.streamingWrite(iter.next().getTripleArray());
			}
			break;

		case GET_WITH_TWO_CMD:
			s1 = BytesUtil.toString(rdr.readArg());
			String s2 = BytesUtil.toString(rdr.readArg());
			out.prepStreamingWrite(ACK_CMD, server.sizeOf(s1, s2) * 3);
			iter = server.iterator(s1, s2);
			while (iter.hasNext()) {
				out.streamingWrite(iter.next().getTripleArray());
			}
			break;
		case DIS_READ_CMD:
			out.write(ACK_CMD, String.valueOf(server.distributedLoadFromHDFS(
					new Path(BytesUtil.toString(rdr.readArg())),
					Integer.parseInt(BytesUtil.toString(rdr.readArg())))));
			break;
		default:
			printHelpMessage(cmd, numArgs, rdr);
		}
	}
}
