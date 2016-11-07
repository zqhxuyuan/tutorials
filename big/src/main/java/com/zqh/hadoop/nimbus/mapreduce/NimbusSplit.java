package com.zqh.hadoop.nimbus.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;

public class NimbusSplit extends InputSplit implements Writable {

	private String host;

	public NimbusSplit() {
		host = null;
	}

	public NimbusSplit(String host) {
		this.host = host;
	}

	@Override
	public long getLength() throws IOException, InterruptedException {
		return 0;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return new String[] { host };
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		host = in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(host);
	}
}
