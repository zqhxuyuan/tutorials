package com.zqh.hadoop.nimbus.mapreduce;

import java.util.HashSet;
import java.util.Set;

import com.zqh.hadoop.nimbus.utils.ICacheletHash;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class NimbusHashPartitioner extends Partitioner<Text, Object> {

	private ICacheletHash hash = ICacheletHash.getInstance();
	private Set<Integer> set = new HashSet<Integer>();

	@Override
	public int getPartition(Text key, Object value, int numPartitions) {
		set.clear();
		hash.getCacheletsFromKey(key.toString(), set, numPartitions, 1);
		return set.iterator().next();
	}
}
