package com.zqh.hadoop.nimbus.master;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.zqh.hadoop.nimbus.server.CacheType;

/**
 * This class stores information about each individual class that will
 * eventually be stored in ZooKeeper.
 */
public class CacheInfo {

	private String name = null;
	private int port = -1;
	private CacheType type = null;
	private String filename = null;
	private int approxNumRecords = 0;
	private float falsePosRate = 0f;
	private byte[] availability = null;

	/**
	 * Initializes a new instance of CacheInfo.<br>
	 * None of the member variables are initialized.
	 */
	public CacheInfo() {
	}

	/**
	 * Initializes a new instance of CacheInfo.<br>
	 * Deserializes the byte stream to read values.
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	public CacheInfo(byte[] bytes) throws IOException {
		ByteArrayInputStream bytestream = new ByteArrayInputStream(bytes);
		DataInputStream strm = new DataInputStream(bytestream);
		name = strm.readUTF();
		port = strm.readInt();
		type = CacheType.valueOf(strm.readUTF());

		filename = strm.readUTF();
		if (filename.equals("null")) {
			filename = null;
		}
		approxNumRecords = strm.readInt();
		falsePosRate = strm.readFloat();

		int len = strm.readInt();
		availability = new byte[len];
		strm.read(availability, 0, len);

		strm.close();
	}

	/**
	 * Serializes this object to a byte array and returns it.
	 * 
	 * @return A byte representation.
	 */
	public byte[] getByteRepresentation() {
		try {
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			DataOutputStream strm = new DataOutputStream(bytestream);

			strm.writeUTF(name);
			strm.writeInt(port);
			strm.writeUTF(type.name());

			if (filename == null) {
				strm.writeUTF("null");
			} else {
				strm.writeUTF(filename);
			}
			strm.writeInt(approxNumRecords);
			strm.writeFloat(falsePosRate);

			strm.writeInt(availability.length);
			strm.write(availability);

			strm.flush();
			strm.close();
			return bytestream.toByteArray();
		} catch (IOException e) {
			// Could this even really happen?
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the name of this Cache.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this Cache.
	 * 
	 * @param name
	 *            The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the port this Cache is running on.
	 * 
	 * @return The port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port this Cache is running on.
	 * 
	 * @param port
	 *            The port.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the type of this Cache.
	 * 
	 * @return The type.
	 */
	public CacheType getType() {
		return type;
	}

	/**
	 * Sets the type of this Cache.
	 * 
	 * @param type
	 *            The type.
	 */
	public void setType(CacheType type) {
		this.type = type;
	}

	/**
	 * Sets the byte array, representing which cachelets are available.
	 * 
	 * @param availability
	 *            The availability byte array.
	 */
	public void setAvailabilityArray(byte[] availability) {
		this.availability = availability;
	}

	/**
	 * Gets the byte array, representing which cachelets are available.
	 * 
	 * @return The byte array.
	 */
	public byte[] getAvailabilityArray() {
		return availability;
	}

	/**
	 * Sets the filename for this Cache.
	 * 
	 * @param filename
	 *            The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Gets the filename for this Cache.
	 * 
	 * @return The filename, or null if not set.
	 */
	public String getFilename() {
		return filename;
	}

	public void setApproxNumRecords(int approxNumRecords) {
		this.approxNumRecords = approxNumRecords;
	}

	public int getApproxNumRecords() {
		return approxNumRecords;
	}

	public void setFalsePosRate(float falsePosRate) {
		this.falsePosRate = falsePosRate;
	}

	public float getFalsePosRate() {
		return falsePosRate;
	}
}
