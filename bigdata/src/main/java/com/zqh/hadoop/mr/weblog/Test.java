package com.zqh.hadoop.mr.weblog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;


/* A simple client that tests the record reader locally.
 * Takes a log file as an argument and passes each line of text to the record reader.
 * This simulates the behavior or the LineRecordReader in the cluster.
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String input = "";
		if (args.length == 1) {
			input = args[0];
		} else {
			System.err.println("Usage: test <file>");
			System.exit(-1);
		}
		CommonWebLogRecordReader cwlrr = new CommonWebLogRecordReader();
		CommonWebLog cwl = new CommonWebLog();
		FileInputStream f;
		try {
			f = new FileInputStream(input);
			DataInputStream in = new DataInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			LongWritable key = new LongWritable(100);
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				cwlrr.setCurrent(key, cwl, new Text(strLine));
				System.out.println(cwl.toString());
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
