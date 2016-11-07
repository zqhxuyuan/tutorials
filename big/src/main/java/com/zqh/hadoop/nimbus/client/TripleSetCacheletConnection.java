package com.zqh.hadoop.nimbus.client;

import java.io.IOException;

import com.zqh.hadoop.nimbus.master.NimbusMaster;
import com.zqh.hadoop.nimbus.utils.BytesUtil;
import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.nativestructs.Triple;
import com.zqh.hadoop.nimbus.server.TripleSetCacheletWorker;

/**
 * Helper class to handle connections to each Cachelet. Used by the
 * TripleSetClient to... well... connect to each Cachelet.
 */
public class TripleSetCacheletConnection extends BaseNimbusClient {

	/*
	 * private final Logger LOG = Logger
	 * .getLogger(TripleSetCacheletConnection.class); private String
	 * cacheletName;
	 */

	/**
	 * Connects to the given host. Automatically gets the port from the Master
	 * based on the given Cache name.
	 * 
	 * @param cacheName
	 *            The Cache to connect to.
	 * @param cacheletName
	 *            The host of the Cachelet.
	 * @throws CacheDoesNotExistException
	 *             If the Cache does not exist.
	 * @throws IOException
	 *             If some bad juju happens.
	 */
	public TripleSetCacheletConnection(String cacheName, String cacheletName)
			throws CacheDoesNotExistException, IOException {
		super(cacheletName, NimbusMaster.getInstance().getCachePort(cacheName));
		super.cacheName = cacheName;
		// this.cacheletName = cacheletName;
		connect();
	}

	/**
	 * Sends a request to the Cachelet to add the given element
	 * 
	 * @param element
	 *            The element to add.
	 * @return If the element was added.
	 * @throws IOException
	 */
	public boolean add(Triple element) throws IOException {
		super.write(TripleSetCacheletWorker.ADD_CMD, element.getTripleArray());
		if (super.in.readCmd() != TripleSetCacheletWorker.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());
		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		}

		throw new IOException("Did not receive a true or false response.");
	}

	public void getAll() throws IOException {
		super.write(TripleSetCacheletWorker.GET_ALL_CMD);
	}

	public void get(String s1) throws IOException {
		super.write(TripleSetCacheletWorker.GET_WITH_ONE_CMD, s1);
	}

	public void get(String s1, String s2) throws IOException {
		super.write(TripleSetCacheletWorker.GET_WITH_TWO_CMD, s1, s2);
	}

	/**
	 * Sends a request to the Cachelet to determine if the given element is a
	 * member of the set.
	 * 
	 * @param element
	 *            The element to request.
	 * @return If the element is a member of the set.
	 * @throws IOException
	 */
	public boolean contains(Triple element) throws IOException {
		super.write(TripleSetCacheletWorker.CONTAINS_CMD,
				element.getTripleArray());
		if (super.in.readCmd() != TripleSetCacheletWorker.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());
		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		}

		throw new IOException("Did not receive a true or false response.");
	}

	/**
	 * Sends a request to the Cachelet to determine if this Cachelet has any
	 * elements.
	 * 
	 * @return If the Cachelet is empty.
	 * @throws IOException
	 *             If an error occurs when sending the request.
	 */
	public boolean isEmpty() throws IOException {
		super.write(TripleSetCacheletWorker.ISEMPTY_CMD);
		if (super.in.readCmd() != TripleSetCacheletWorker.ACK_CMD) {
			throw new IOException("Did not receive ACK_CMD");
		}

		super.in.readNumArgs();

		String response = BytesUtil.toString(super.in.readArg());
		if (response.equals("true")) {
			return true;
		} else if (response.equals("false")) {
			return false;
		}

		throw new IOException("Did not receive a true or false response.");
	}
}