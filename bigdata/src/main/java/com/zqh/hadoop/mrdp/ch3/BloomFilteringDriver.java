package com.zqh.hadoop.mrdp.ch3;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.apache.hadoop.util.bloom.Key;

/**
 * Chapter 3 Filter Patterns : Bloom Filter
 *
 * Problem: Given a list of user’s comments, filter out a majority of the comments that do not contain a particular keyword
 */
public class BloomFilteringDriver {

	public static class BloomFilteringMapper extends Mapper<Object, Text, Text, NullWritable> {

		private BloomFilter filter = new BloomFilter();

        /**
         * 在Map开始时,读取BloomFilter文件到BloomFilter中
         * 注意: BloomFilter要事先运行,运行BloomFilterDriver的main函数
         * 会在HDFS的DistributedCache中生成文件
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			URI[] files = DistributedCache.getCacheFiles(context.getConfiguration());

			// if the files in the distributed cache are set
			if (files != null && files.length == 1) {
				System.out.println("Reading Bloom filter from: " + files[0].getPath());

				// Open local file for read.
				DataInputStream strm = new DataInputStream(new FileInputStream(files[0].getPath()));

				// Read into our Bloom filter.
				filter.readFields(strm);
				strm.close();
			} else {
				throw new IOException("Bloom filter file not set in the DistributedCache.");
			}
		}

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// Parse the input into a nice map.
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

			// Get the value for the comment
			String comment = parsed.get("Text");

			// If it is null, skip this record
			if (comment == null) return;

			StringTokenizer tokenizer = new StringTokenizer(comment);
			// For each word in the comment
			while (tokenizer.hasMoreTokens()) {
				// Clean up the words
				String cleanWord = tokenizer.nextToken().replaceAll("'", "").replaceAll("[^a-zA-Z]", " ");

				// If the word is in the filter, output it and break
                // 判断是否在Bloom Filter里. 如果BF判断不存在,则一定不存在. 有一种false positive:
                // BF判断存在, 但是实际不存在.
                // BF是ifExist的快速版本. 由于只是过滤数据. 如果存在的话,数据仍然原封不动输出
                // key为数据本身, value为NULL. 而且没有reduce过程.
				if (cleanWord.length() > 0 && filter.membershipTest(new Key(cleanWord.getBytes()))) {
					context.write(value, NullWritable.get());
					break;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: BloomFiltering <in> <cachefile> <out>");
			System.exit(1);
		}

		FileSystem.get(conf).delete(new Path(otherArgs[2]), true);

		Job job = new Job(conf, "StackOverflow Bloom Filtering");
		job.setJarByClass(BloomFilteringDriver.class);
		job.setMapperClass(BloomFilteringMapper.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

		DistributedCache.addCacheFile(
				FileSystem.get(conf).makeQualified(new Path(otherArgs[1])).toUri(),
                job.getConfiguration());

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
