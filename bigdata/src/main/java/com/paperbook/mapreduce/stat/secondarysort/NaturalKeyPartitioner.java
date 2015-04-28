package com.paperbook.mapreduce.stat.secondarysort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class NaturalKeyPartitioner extends Partitioner<SortKeyPair, Text>{

	@Override
	public int getPartition(SortKeyPair key, Text value, int numPartitions) {
		// % is hash partition, can't make sure bigger count go to reducer with small id.
		int count = key.getCount();
		if (count <= 5) {
			return 0;
		} else if (count > 5 && count <= 10) {
			return 1;
		} else if (count > 10 && count <= 15) {
			return 2;
		} else {
			return 3;
		}
	}

}
