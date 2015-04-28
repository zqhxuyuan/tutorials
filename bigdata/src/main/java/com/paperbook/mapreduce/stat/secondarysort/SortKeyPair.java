package com.paperbook.mapreduce.stat.secondarysort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class SortKeyPair implements WritableComparable<SortKeyPair> {
	private int count = 0;
	private long avgts = 0;

	public SortKeyPair() {

	}

	/**
	 * 
	 * @param count
	 * @param timestamp
	 *            Average timestamp
	 */
	public SortKeyPair(int count, long avgts) {
		super();
		this.count = count;
		this.avgts = avgts;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getAvgts() {
		return avgts;
	}

	public void setAvgts(long avgts) {
		this.avgts = avgts;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.count);
		out.writeLong(this.avgts);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.count = in.readInt();
		this.avgts = in.readLong();
	}

	/**
	 * We want sort in descending count and descending avgts
	 */
	@Override
	public int compareTo(SortKeyPair o) {
		int res = this.count < o.getCount() ? 1
				: (this.count == o.getCount() ? 0 : -1);

		if (res == 0) {
			res = this.avgts < o.getAvgts() ? 1
					: (this.avgts == o.getAvgts() ? 0 : -1);
		}

		return res;
	}

	@Override
	public int hashCode() {
		return Integer.MAX_VALUE - this.count;
		//return this.count;
	}

	@Override
	public String toString() {
		return this.count + "," + this.avgts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof SortKeyPair) {
			SortKeyPair s = (SortKeyPair) obj;
			return this.count == s.getCount() && this.avgts == s.getAvgts();
		} else {
			return false;
		}
	}

}
