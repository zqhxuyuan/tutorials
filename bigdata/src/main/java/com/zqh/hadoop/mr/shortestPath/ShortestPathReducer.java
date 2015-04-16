package com.zqh.hadoop.mr.shortestPath;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ShortestPathReducer extends MapReduceBase implements
		Reducer<Text, NodeWritable, Text, NodeWritable> {

	@Override
	public void reduce(Text key, Iterator<NodeWritable> values,
			OutputCollector<Text, NodeWritable> output, Reporter reporter)
			throws IOException {

		// The nodes are either the node definition or possible distances to the
		// node.
		// For a given key, there should be a single node definition and
		// multiple distances.
		// Find the node definition and the minimum distance.

		int minDistance = Integer.MAX_VALUE;
		NodeWritable theNode = new NodeWritable();
		while (values.hasNext()) {
			NodeWritable nextNode = values.next();
			if (nextNode.isNode()) {
				// There should only be one per key since the mapper emits the node once to
				// preserve the graph from one iteration to another.
				theNode.setNode(nextNode);
			}
			if (nextNode.isDistanceKnown()
					&& nextNode.getDistance() < minDistance) {
				minDistance = nextNode.getDistance();
			}

		}
		if (minDistance != Integer.MAX_VALUE) {
			// We found the minimum
			theNode.setDistance(minDistance);

			// Increment a counter indicating that the shortest path to the
			// node has been found.
			reporter.getCounter("node", "nodeDistanceSet").increment(1);
		}

		// output the node with the distance updated.
		output.collect(key, theNode);
	}
}
