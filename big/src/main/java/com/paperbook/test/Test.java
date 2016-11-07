package com.paperbook.test;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.util.Bytes;

public class Test {
	
	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();		
		HTableInterface literaturesTable = new HTable(conf, "pb_literatures");
		
		Scan s = new Scan();
		s.addFamily(Bytes.toBytes("info"));
		ResultScanner rs = literaturesTable.getScanner(s);
		for (Result r : rs) {
			byte[] b = r.getValue(Bytes.toBytes("info"), Bytes.toBytes("title"));
			System.out.println(Bytes.toString(b));
		}
		
		
	}

}
