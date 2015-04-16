package com.zqh.hadoop.mr.shortestPath;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ShortestPathMapper extends MapReduceBase implements
		Mapper<Text, Text, Text, NodeWritable> {
	//public static final Logger LOGGER = Logger.getLogger("shortestpath");

	@Override
	public void configure(JobConf conf) {
		//LOGGER.setLevel(Level.INFO);
	}
	@Override
	public void map(Text key, Text value,
			OutputCollector<Text, NodeWritable> output,
			Reporter reporter) throws IOException {
		
		// Turn the value into a node -- the key and value are text
		NodeWritable theNode = NodeWritable.parse(key,value);
		
		// emit the node to use in the next iteration
		//LOGGER.log(Level.INFO, "map called on "+key+" / "+value);
		//LOGGER.log(Level.INFO, "  emitting "+key+" / "+theNode);
		output.collect(key, theNode);
		
		// if the distance to this node from the source is known, then
		// emit a reference node for the neighbor nodes with distance + 1
		if (theNode.isDistanceKnown()) {
			int distance = theNode.getDistance();
			Iterator<Text> neighbors = theNode.adjacencyList.iterator();
			//LOGGER.log(Level.INFO, "  distance is "+distance);
			while (neighbors.hasNext()) {
				Text neighborId = neighbors.next();
				NodeWritable neighborReference = new NodeWritable(neighborId,distance+1);
				//LOGGER.log(Level.INFO, "  emitting reference "+neighborId+" / "+neighborReference);
				output.collect(neighborId, neighborReference);
			}
		} else {
			//LOGGER.log(Level.INFO, "  distance is not known for node");
		}

	}
}