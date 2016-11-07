package com.zqh.hadoop.mrdp.ch3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.hadoop.util.hash.Hash;

/**
 * 训练布隆过滤器
 */
public class BloomFilterDriver {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err.println("Usage: BloomFilterWriter <inputfile> <nummembers> <falseposrate> <bfoutfile>");
			System.exit(1);
		}

        // 文件系统接口, 用于读取原始文件, 并将布隆过滤器的训练结果写到HDFS中
		FileSystem fs = FileSystem.get(new Configuration());

		// Parse command line arguments 布隆过滤器的一些参数, 用于控制误差率等选项
		Path inputFile = new Path(otherArgs[0]);
		int numMembers = Integer.parseInt(otherArgs[1]);
		float falsePosRate = Float.parseFloat(otherArgs[2]);
		Path bfFile = new Path(otherArgs[3]); //输出结果是文件,放在HDFS中

		// Calculate our vector size and optimal K value based on approximations
		int vectorSize = getOptimalBloomFilterSize(numMembers, falsePosRate);
		int nbHash = getOptimalK(numMembers, vectorSize);

		// create new Bloom filter
		BloomFilter filter = new BloomFilter(vectorSize, nbHash, Hash.MURMUR_HASH);

		// Open file for read
		System.out.println("Training Bloom filter of size " + vectorSize
				+ " with " + nbHash + " hash functions, " + numMembers
				+ " approximate number of records, and " + falsePosRate
				+ " false positive rate");

        // 输入数据的每一行
		String line = null;
        // 训练的记录数
		int numRecords = 0;
		for (FileStatus status : fs.listStatus(inputFile)) {
			BufferedReader rdr;
			// if file is gzipped, wrap it in a GZIPInputStream
			if (status.getPath().getName().endsWith(".gz")) {
				rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(fs.open(status.getPath()))));
			} else {
				rdr = new BufferedReader(new InputStreamReader(fs.open(status.getPath())));
			}

			System.out.println("Reading " + status.getPath());
			while ((line = rdr.readLine()) != null) {
				filter.add(new Key(line.getBytes()));
				++numRecords;
			}

			rdr.close();
		}

		System.out.println("Trained Bloom filter with " + numRecords + " entries.");

        // 布隆过滤器训练完毕, 序列化布隆过滤器对象到HDFS中
		System.out.println("Serializing Bloom filter to HDFS at " + bfFile);
		FSDataOutputStream strm = fs.create(bfFile);
		filter.write(strm);

		strm.flush();
		strm.close();

		System.out.println("Done training Bloom filter.");
	}

    /**
     *
     * @param numRecords 记录数
     * @param falsePosRate 允许的误差
     * @return
     */
	public static int getOptimalBloomFilterSize(int numRecords, float falsePosRate) {
		int size = (int) (-numRecords * (float) Math.log(falsePosRate) / Math.pow(Math.log(2), 2));
		return size;
	}

	public static int getOptimalK(float numMembers, float vectorSize) {
		return (int) Math.round(vectorSize / numMembers * Math.log(2));
	}
}
