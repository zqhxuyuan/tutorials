package com.zqh.hadoop.mr.weblog;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.io.WritableComparable;

public class CommonWebLog implements WritableComparable<CommonWebLog> {

	public static String MISSING = "-";
	public static Date MISSINGDATE = new Date();
	public static Host MISSINGHOST = new Host(MISSING);
	public static int MISSINGINT = -1;
	
	private Host host = MISSINGHOST;
	private String remoteLogName = MISSING;
	private String userId = MISSING;
	private Date date = MISSINGDATE;
	private String operation = MISSING;
	private String resource = MISSING;
	private String httpVersion = MISSING;
	private int returnCode = MISSINGINT;
	private long returnObjectSize = MISSINGINT;
	
	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");


	@Override
	public void readFields(DataInput in) throws IOException {
		readHost(in);
		setRemoteLogName(in.readUTF());
		setUserId(in.readUTF());
		setDate(in.readUTF());
		setOperation(in.readUTF());
		setResource(in.readUTF());
		setHttpVersion(in.readUTF());
		setReturnCode(in.readInt());
		setReturnObjectSize(in.readLong());
	}



	@Override
	public void write(DataOutput out) throws IOException {
		writeHost(out);
		out.writeUTF(getRemoteLogName());
		out.writeUTF(getUserId());
		out.writeUTF(getDateAsString());
		out.writeUTF(getOperation());
		out.writeUTF(getResource());
		out.writeUTF(getHttpVersion());
		out.writeInt(getReturnCode());
		out.writeLong(getReturnObjectSize());
	}

	@Override
	public int compareTo(CommonWebLog o) {
		// Cannot distinguish two separate requests for the same operation on
		// the same resource from same user received at the same second.
		int compareDate, compareIPAddress, compareRemoteLogName, compareUserId, compareOperation, compareResource, compareReturnCode;

		if ((compareDate = getDate().compareTo(o.getDate())) != 0)
			return compareDate;
		if ((compareIPAddress = getHost().compareTo(o.getHost())) != 0)
			return compareIPAddress;
		if ((compareRemoteLogName = getRemoteLogName().compareTo(
				o.getRemoteLogName())) != 0)
			return compareRemoteLogName;
		if ((compareUserId = getUserId().compareTo(o.getUserId())) != 0)
			return compareUserId;
		if ((compareOperation = getOperation().compareTo(o.getOperation())) != 0)
			return compareOperation;
		if ((compareResource = getResource().compareTo(o.getResource())) != 0)
			return compareResource;
		if ((compareReturnCode = compareTo(getReturnCode(), o.getReturnCode())) != 0)
			return compareReturnCode;
		return compareTo(getReturnObjectSize(), o.getReturnObjectSize());

	}

	private int compareTo(long x, long y) {
		if (x == y)
			return 0;
		if (x < y)
			return -1;
		return 1;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public void setHost(String hostString) {
		if (hostString==null || hostString.equals(MISSING)) {
			host = MISSINGHOST;
		} else {
			host = new Host(hostString);
		}				
	}
	
	private void readHost(DataInput in) throws IOException {
		setHost(in.readUTF());
	}

	private void writeHost(DataOutput out) throws IOException {
		if (host==MISSINGHOST) {
			out.writeUTF(MISSING);
		} else {
			host.write(out);
		}
	}
	
	public String getRemoteLogName() {
		return remoteLogName;
	}

	public void setRemoteLogName(String rlname) {
		if (rlname == null) {
			rlname = MISSING;
		}
		remoteLogName = rlname;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String uid) {
		if (uid == null) {
			uid = MISSING;
		}
		userId = uid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private String getDateAsString() {
		if (date == null || date == MISSINGDATE) {
			return MISSING;
		} else {
			return dateFormatter.format(date);
		}
	}
	
	private void setDate(String dateStr) {
		if (dateStr == null || dateStr.equals(MISSING)) {
			date = MISSINGDATE;
		} else {
			try {
				setDate(dateFormatter.parse(dateStr));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String op) {
		if (op == null ) {
			op = MISSING;
		} 
		operation = op;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resrc) {
		if (resrc == null ) {
			resrc = MISSING;
		}
		resource = resrc;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpv) {
		if (httpv == null ) {
			httpv = MISSING;
		} 
		httpVersion = httpv;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public long getReturnObjectSize() {
		return returnObjectSize;
	}

	public void setReturnObjectSize(long returnObjectSize) {
		this.returnObjectSize = returnObjectSize;
	}

	public void clear() {
		setDate(MISSINGDATE);
		setHttpVersion(MISSING);
		setHost(MISSINGHOST);
		setOperation(MISSING);
		setResource(MISSING);
		setReturnCode(MISSINGINT);
		setReturnObjectSize(MISSINGINT);
		setUserId(MISSING);
		setRemoteLogName(MISSING);
	}
	

	public String getResourceType() {
		String result = "other";
		String resource = getResource();
		if (resource != null) {
			if (resource.endsWith(".jpg")) {
				result = "jpg";
			}
			else if (resource.endsWith(".gif")) {
				result = "gif";
			}
		}
		return result;
	}
	
	/* Representing the weblog as a string, returns it to its original format, 
	 * as found in the weblog file.
	 */
	
	public String toString() {
		return toString(" ");
	}
	
	public String toString(String kvs) {
		return asString(getHost()) + kvs + 
				getRemoteLogName() + kvs +
				getUserId() + kvs+
				"[" + asString(getDate()) + "]"+kvs+"\"" +
				getOperation() + kvs + 
				getResource()+ kvs + 
				getHttpVersion() + "\"" + kvs + 
				asString(getReturnCode())+ kvs + 
				asString(getReturnObjectSize());
	}

	private String asString(long returnObjectSize) {
		if (returnObjectSize==MISSINGINT) {
			return MISSING;
		} else {
			return Long.toString(returnObjectSize);
		}
	}

	private String asString(int returnCode) {
		if (returnCode == MISSINGINT) {
			return MISSING;
		} else {
			return Integer.toString(returnCode);
		}
	}

	private String asString(Date date) {
		if (date == MISSINGDATE) {
			return MISSING;
		}
		else {
			return dateFormatter.format(date);
		}
	}

	private String asString(Host host) {
		if (host == MISSINGHOST)
			return MISSING;
		else
			return host.toString();
	}
	

}
