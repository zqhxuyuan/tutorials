package com.zqh.hadoop.mr.shortestPath;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

// There are two types of nodes -- complete nodes and references to complete nodes.
// Complete nodes have an id, a distance and an adjacency list.
// The distance to a complete node may not be known.
// References just have an id and a candidate distance.
// The candidate distance associated with a reference is known -- it is created when processing a node

public class NodeWritable implements Writable {
	private final static byte reference = 1;
	private final static byte node = 2;
	private byte type;

	private int distance;
	private Text id;
	public ArrayList<Text> adjacencyList;

	// Used by Hadoop MapReduce serialization
	public NodeWritable() {
	}

	// for constructing a reference node
	public NodeWritable(Text id, int distance) {
		this.distance = distance;
		this.id = id;
		type = reference;
	}

	// Nodes emitted by the mapper are serialized with the write method.
	// Mappers emit both complete nodes and node references.
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(distance);
		out.writeUTF(id.toString());
		out.writeByte(type);
		if (isNode()) {
			out.writeInt(adjacencyList.size());
			for (int i = 0; i < adjacencyList.size(); i++) {
				out.writeUTF(adjacencyList.get(i).toString());
			}
		}
	}

	// Nodes, both complete nodes and references, are deserialized with the readFields method
	@Override
	public void readFields(DataInput in) throws IOException {
		distance = in.readInt();
		id = new Text(in.readUTF());
		type = in.readByte();
		if (isNode()) {
			int length = in.readInt();
			adjacencyList = new ArrayList<Text>();
			for (int i = 0; i < length; i++) {
				adjacencyList.add(new Text(in.readUTF()));
			}
		}
	}

	// Graph is represented in the file as KeyValueTextInputFormat so that
	// it can be created with a text editor. The parse() method instantiates
	// a node
	static public NodeWritable parse(Text key, Text value) {
		NodeWritable node = new NodeWritable();
		node.id = key;
		node.distance = -1;
		node.type = NodeWritable.node;
		String[] tokens = value.toString().split(",");
		if (tokens.length>0) {
			node.distance = Integer.parseInt(tokens[0]);
			node.adjacencyList = new ArrayList<Text>();
			for (int i=1; i<tokens.length; i++) {
				node.adjacencyList.add(new Text(tokens[i]));
			}
		}
		return node;
	}

	@Override
	public String toString() {
		if (this.isReference()) return Integer.toString(distance);
		String node="";
		Iterator<Text> neighbors = adjacencyList.iterator();
		if (neighbors.hasNext()) {
			node = distance+",";
		} else {
			node = Integer.toString(distance);
		}
		while (neighbors.hasNext()) {
			String neighbor = neighbors.next().toString();
			if (neighbors.hasNext()) {
				node = node + neighbor+",";
			} else {
				node = node + neighbor;
			}
		}
		return node;
	}

	public boolean isNode() {
		return type == node;
	}

	public boolean isReference() {
		return type == reference;
	}

	public boolean isDistanceKnown() {
		return distance >= 0;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setNode(NodeWritable node) {
		this.distance = node.getDistance();
		this.id = node.id;
		this.type = node.type;
		if (isNode()) {
			adjacencyList = new ArrayList<Text>();
			for (int i = 0; i < node.adjacencyList.size(); i++) {
				adjacencyList.add(new Text(node.adjacencyList.get(i)));
			}
		}
	}

}
