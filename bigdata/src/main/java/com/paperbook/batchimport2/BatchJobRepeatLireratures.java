package com.paperbook.batchimport2;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;


public class BatchJobRepeatLireratures {

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		Configuration conf = HBaseConfiguration.create();
		HTableInterface literaturesTable = new HTable(conf, "pb_mr_literatures_repeat");
		LiteratureGenerator literatureGenerator = new LiteratureGenerator();
		literatureGenerator.createLiteratures(100000);
		ArrayList<Put> literaturePutList = literatureGenerator.createPutList();
		ArrayList<Put> literatureRepeatPutList = literatureGenerator
				.createRandomRepeatPutList();
		literaturesTable.put(literaturePutList);
		literaturesTable.put(literatureRepeatPutList);
		literaturesTable.close();
		System.out.println("Insert literatures = " + literaturePutList.size() + ", repeat = " + literatureRepeatPutList.size() +  "  ,done with time: " + (System.currentTimeMillis() - start));
		literaturePutList = null;
		literatureRepeatPutList = null;
	}

}
