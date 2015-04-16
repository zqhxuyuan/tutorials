package com.zqh.storm.logging.common;

import backtype.storm.tuple.Tuple;
import com.mongodb.DBObject;

import java.io.Serializable;


public abstract class MongoToTupleFormatter implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public abstract DBObject mapTupleToDBObject(DBObject object, Tuple tuple);
	
}
