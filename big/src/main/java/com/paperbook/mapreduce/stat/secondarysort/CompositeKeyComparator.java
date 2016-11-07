package com.paperbook.mapreduce.stat.secondarysort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CompositeKeyComparator extends WritableComparator{

	public CompositeKeyComparator () {
		super(SortKeyPair.class, true);
	}
	
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		SortKeyPair s1 = (SortKeyPair)a;
		SortKeyPair s2 = (SortKeyPair)b;
		
		return s1.compareTo(s2);
	}

	
}
