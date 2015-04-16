package com.zqh.hadoop.mr.weblog;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Host implements WritableComparable<Host> {

	private String address;
	
	public Host(String hostString) {
		setAddress(hostString);
	}
	
	public Host() {
		address = "";
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		setAddress(in.readUTF());
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(getAddress());		
	}

	@Override
	public int compareTo(Host o) {
		return address.compareTo(o.toString());
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void clear() {
		setAddress(null);
	}

	public String toString() {
		return getAddress();
	}

}
