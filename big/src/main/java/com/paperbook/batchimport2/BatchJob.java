package com.paperbook.batchimport2;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;


public class BatchJob {

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		Configuration conf = HBaseConfiguration.create();
		HTableInterface literaturesTable = new HTable(conf, "pb_mr_literatures");
		LiteratureGenerator literatureGenerator = new LiteratureGenerator();
		literatureGenerator.createLiteratures(100000);
		ArrayList<Put> literaturePutList = literatureGenerator.createPutList();
		literaturesTable.put(literaturePutList);
		literaturePutList = null;
		literaturesTable.close();
		System.out.println("Insert literatures done with time: " + (System.currentTimeMillis() - start));
		
		// In case of out of memory, so insert roughly 350000 each time. 2~5 comments for 100000 literatures each time
		HTableInterface commentsTable = new HTable(conf, "pb_mr_comments");
		CommentsGenerator commentsGenerator = new CommentsGenerator(literatureGenerator.dictionary, literatureGenerator.userlist, literatureGenerator.literatures);
		ArrayList<Put> commentPutList = commentsGenerator.createComments();
		commentsTable.put(commentPutList);	
		System.out.println("Insert comments number = " + commentPutList.size() +  " done with time: " + (System.currentTimeMillis() - start));
		
		commentPutList = null;
		commentPutList = commentsGenerator.createComments();
		commentsTable.put(commentPutList);
		System.out.println("Insert comments number = " + commentPutList.size() +  " done with time: " + (System.currentTimeMillis() - start));
		
		commentPutList = null;
		commentPutList = commentsGenerator.createComments();
		commentsTable.put(commentPutList);
		System.out.println("Insert comments number = " + commentPutList.size() +  " done with time: " + (System.currentTimeMillis() - start));
		
		commentPutList = null;
		commentPutList = commentsGenerator.createComments();
		commentsTable.put(commentPutList);
		System.out.println("Insert comments number = " + commentPutList.size() +  " done with time: " + (System.currentTimeMillis() - start));
		commentsTable.close();

		long end = System.currentTimeMillis();
		System.out.println("All insertion done , running time: " + (end - start));
	}

}
