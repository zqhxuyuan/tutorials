package com.zqh.hadoop.mrdp.ch5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * There is an additional restriction that a replicated join is really useful only for an inner
 or a left outer join where the large data set is the “left” data set.
 ReplicatedJoin只对inner join或者left join有效. 这种join要确保数据量大的要在左边

 The other join types require a reduce phase to group the “right” data set with the entirety of the left data set.
 其他类型的join需要reduce阶段, 而reduce需要对右侧的数据分组. 就需要左边全部的数据.
 因为左边的数据量很大, 就需要map阶段将数据传到reduce才可以进行计算. 显然传输大数据量对这种类型的join不适合

 Although there may not be a match for the data stored in memory for a given map task,
 there could be match in another input split.
 Because of this, we will restrict this pattern to inner and left outer joins.

 The output is a number of part files equivalent to the number of map tasks. The part
 files contain the full set of joined records. If a left outer join is used, the input to the
 MapReduce analytic will be output in full, with possible null values.
 */
public class ReplicatedJoinDriver {

	public static class ReplicatedJoinMapper extends Mapper<Object, Text, Text, Text> {

        // 读取分布式缓存里文件到内存中. 存放的是<userId, Comment内容>
		private HashMap<String, String> userIdToInfo = new HashMap<String, String>();

		private Text outvalue = new Text();
		private String joinType = null;

        /**
         * A replicated join is an extremely useful, but has a strict size limit on all but one of the
         data sets to be joined. All the data sets except the very large one are essentially read into
         memory during the setup phase of each map task, which is limited by the JVM heap.
         除了大数据量一端的, 其他数据会被读取到内存中.
         因为大数据量在左侧, 其他数据在右侧. 在没有reduce的情况下, 不需要传递大数据量的一端.
         也就是说join操作直接在mapper完成. 没有reduce!

         为什么不需要reduce?
         Instead of filtering out data that will never be joined on the reduce side,
         the data is joined in the map phase.
         因为进行join操作, 有些数据会被过滤掉(比如innner join会把两边不存在的数据过滤掉)
         为了防止数据传输到reduce,但是因为join操作而被过滤, 这样的数据实际是不需要传输的.
         所以我们要在map端就进行join操作. 相当于数据在mapper端就进行了过滤.
         */
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            if (files == null || files.length == 0) throw new RuntimeException("User information is not set in DistributedCache");

            // Read all files in the DistributedCache
            for (Path p : files) {
                BufferedReader rdr = new BufferedReader(new InputStreamReader(
                     new GZIPInputStream(new FileInputStream(new File(p.toString())))));

                String line;
                // For each record in the user file
                while ((line = rdr.readLine()) != null) {

                    // Get the user ID for this record
                    Map<String, String> parsed = MRDPUtils.transformXmlToMap(line);
                    String userId = parsed.get("Id");

                    if (userId != null) {
                        // Map the user ID to the record
                        userIdToInfo.put(userId, line);
                    }
                }
            }
			// Get the join type
			joinType = context.getConfiguration().get("join.type");
		}

        /**
         * The mapper is responsible for reading all files from the distributed cache during
         the setup phase and storing them into in-memory lookup tables. After this setup
         phase completes, the mapper processes each record and joins it with all the data
         stored in-memory. If the foreign key is not found in the in-memory structures, the
         record is either omitted or output, based on the join type.
         */

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			// Parse the input string into a nice map
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
			String userId = parsed.get("UserId");
			if (userId == null) return;

            // mapper的value是我们的输入数据, 即数据量大的一端.
            // 解析出value里的userId, 判断是否在内存中. 如果存在, 输出! --> 这种情况术语inner join
            // LargeDataSide join MemorySide
			String userInformation = userIdToInfo.get(userId);

			// If the user information is not null, then output
			if (userInformation != null) {
				outvalue.set(userInformation);
				context.write(value, outvalue);
			} else if (joinType.equalsIgnoreCase("leftouter")) {
				// If we are doing a left outer join, output the record with an empty value
                // LargeDataSide left join MemorySide
                // 尽管输入数据的userId不在内存中. 但是因为是输入端进行left join, 所以结果必须包含输入端
				context.write(value, new Text(""));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err
					.println("Usage: ReplicatedJoin <user data> <comment data> <out> [inner|leftouter]");
			System.exit(1);
		}

		String joinType = otherArgs[3];
		if (!(joinType.equalsIgnoreCase("inner") || joinType
				.equalsIgnoreCase("leftouter"))) {
			System.err.println("Join type not set to inner or leftouter");
			System.exit(2);
		}

		// Configure the join type
		Job job = new Job(conf, "Replicated Join");
		job.getConfiguration().set("join.type", joinType);
		job.setJarByClass(ReplicatedJoinDriver.class);

		job.setMapperClass(ReplicatedJoinMapper.class);
		job.setNumReduceTasks(0);

		TextInputFormat.setInputPaths(job, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Configure the DistributedCache
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),
				job.getConfiguration());

		DistributedCache.setLocalFiles(job.getConfiguration(), otherArgs[0]);

		System.exit(job.waitForCompletion(true) ? 0 : 3);
	}
}
