package com.zqh.hadoop.mrdp.ch2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class SmarterMedianStdDevDriver {

	public static class SOMedianStdDevMapper extends
			Mapper<Object, Text, IntWritable, SortedMapWritable> {

		private IntWritable commentLength = new IntWritable();
		private static final LongWritable ONE = new LongWritable(1);
		private IntWritable outHour = new IntWritable();

		private final static SimpleDateFormat frmt = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS");

		@SuppressWarnings("deprecation")
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			// Parse the input string into a nice map
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

			// Grab the "CreationDate" field,
			// since it is what we are grouping by
			String strDate = parsed.get("CreationDate");

			// Grab the comment to find the length
			String text = parsed.get("Text");

			// .get will return null if the key is not there
			if (strDate == null || text == null) {
				// skip this record
				return;
			}

			try {
				// get the hour this comment was posted in
				Date creationDate = frmt.parse(strDate);
				outHour.set(creationDate.getHours());

				commentLength.set(text.length());

                /**
                 * 每个comment的长度分别是: 1, 1, 1, 1, 2, 2, 3, 4, 5, 5, 5
                 * SortedMapWritable记录每种类别的长度和次数: 1→4, 2→2, 3→1, 4→1, 5→3
                 */
				SortedMapWritable outCommentLength = new SortedMapWritable();
				outCommentLength.put(commentLength, ONE);

				// write out the user ID with min max dates and count
				context.write(outHour, outCommentLength);

			} catch (ParseException e) {
				System.err.println(e.getMessage());
				return;
			}
		}
	}

    /**
     Combiner optimization. Unlike the previous examples, the combiner for this algorithm is
     different from the reducer. While the reducer actually calculates the median and stan‐
     dard deviation, the combiner aggregates the SortedMapWritable entries for each local
     map’s intermediate key/value pairs. The code to parse through the entries and aggregate
     them in a local map is identical to the reducer code in the previous section. Here, a
     HashMap is used instead of a TreeMap , because sorting is unnecessary and a HashMap is
     typically faster. While the reducer uses this map to calculate the median and standard
     deviation, the combiner uses a SortedMapWritable in order to serialize it for the reduce phase.
     */
	public static class SOMedianStdDevCombiner extends
            Reducer<IntWritable, SortedMapWritable, IntWritable, SortedMapWritable> {

		@SuppressWarnings("rawtypes")
		protected void reduce(IntWritable key, Iterable<SortedMapWritable> values, Context context) throws IOException, InterruptedException {

			SortedMapWritable outValue = new SortedMapWritable();

            /**
             * Reduce的value是一个Map: <Comment'Length, Comment'Count>, 要返回的也是一个Map
             * 不过不同的是要将多个Map汇聚成一个Map.
             *
             * Map1:<1,4>, <2,2>
             * Map2:<1,3>, <3,2>  ==> 第一层for循环
             * Map3:<2,1>, <3,1>
             *
             *     ==> Map<1,7>
             *         Map<2,3>   ==> 第二层for循环
             *         Map<3,3>
             */
			for (SortedMapWritable v : values) {
				for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
					LongWritable count = (LongWritable) outValue.get(entry.getKey());

					if (count != null) {
						count.set(count.get() + ((LongWritable) entry.getValue()).get());
					} else {
						outValue.put(entry.getKey(), new LongWritable(((LongWritable) entry.getValue()).get()));
					}
				}
			}

			context.write(key, outValue);
		}
	}

	public static class SOMedianStdDevReducer extends
            Reducer<IntWritable, SortedMapWritable, IntWritable, MedianStdDevTuple> {

		private MedianStdDevTuple result = new MedianStdDevTuple();
		private TreeMap<Integer, Long> commentLengthCounts = new TreeMap<Integer, Long>();

		@SuppressWarnings("rawtypes")
		@Override
		public void reduce(IntWritable key, Iterable<SortedMapWritable> values,
				Context context) throws IOException, InterruptedException {

			float sum = 0;
			long totalComments = 0;
			commentLengthCounts.clear();
			result.setMedian(0);
			result.setStdDev(0);

            /**
             * 1→4, 2→2, 3→1, 4→1, 5→3
             * Key是长度, Value是出现的次数
             * 总的Comments数量 = Value相加
             * 总的长度 = Key*Value相加
             */
			for (SortedMapWritable v : values) {
				for (Entry<WritableComparable, Writable> entry : v.entrySet()) {
					int length = ((IntWritable) entry.getKey()).get();
					long count = ((LongWritable) entry.getValue()).get();

					totalComments += count;
					sum += length * count;

                    /**
                     * commentLengthCounts要重新赋值. 因为不同Mapper之间可能存在相同的Key
                     * 比如Mapper1: 1->4, 2->2
                     *    Mapper2: 1->2, 3->4
                     * 则要将相同长度的Comment汇聚在一起
                     */

                    /*
					Long storedCount = commentLengthCounts.get(length);
					if (storedCount == null) {
						commentLengthCounts.put(length, count);
					} else {
						commentLengthCounts.put(length, storedCount + count);
					}
                    */
                    commentLengthCounts.put(length, commentLengthCounts.get(length) == null ?
                            count : commentLengthCounts.get(length)+count);
				}
			}

            // 最中间元素的索引=总个数/2
			long medianIndex = totalComments / 2L;
			long previousCommentCount = 0;  // 前一个Comment的数量
			long commentCount = 0;          // 目前为止, 所有Comment的数量
			int prevKey = 0;                //前一个Comment的长度

            /**
             * 1→4, 2→2, 3→1, 4→1, 5→3
             *
             * 0  1  2  3  4  5  6  7  8  9  10  INDEX, TOTAL-LENGTH=11, SO, MID-INDEX=11/2=5
             * 1, 1, 1, 1, 2, 2, 3, 4, 5, 5, 5   COMMENT'LENGTH
             *                |
             *             median
             * medianIndex = totalComments/2 = 5
             * 因为TreeMap的Key,Value其中的Value是Key出现的次数(数量)
             * 第一个Key=1,value=4,则commentCount += 4 < 5, 则继续下一个key
             * 第二个key=2,value=2,则commentCount = 4+2 = 6, 现在满足4<=5<6, 说明中位数就在key=2这里面
             */
			for (Entry<Integer, Long> entry : commentLengthCounts.entrySet()) {
				commentCount = previousCommentCount + entry.getValue();

                // Iterated to find the keys that satisfy the condition
                // previousCommentCount ≤ medianIndex < commentCount
				if (previousCommentCount <= medianIndex && medianIndex < commentCount) {
                    // if there is an even number of comments and medianIndex is equivalent to previousComment ,
                    // the median is reset to the average of the previous length and current length.
                    // Otherwise, the median is simply the current comment length.
					if (totalComments % 2 == 0) {
						if (previousCommentCount == medianIndex) {
							result.setMedian((float) (entry.getKey() + prevKey) / 2.0f);
						} else {
							result.setMedian(entry.getKey());
						}
					} else {
						result.setMedian(entry.getKey());
					}
					break;
				}
				previousCommentCount = commentCount;
				prevKey = entry.getKey();
			}

			float mean = sum / totalComments; // 这个计算的是平均值

            // calculate standard deviation 计算标准差
			float sumOfSquares = 0.0f;
			for (Entry<Integer, Long> entry : commentLengthCounts.entrySet()) {
                // 要乘上entry.value即次数, 实际上是多个相同长度的Comment相加 a+a+a = a*3
				sumOfSquares += (entry.getKey() - mean) * (entry.getKey() - mean) * entry.getValue();
			}

			result.setStdDev((float) Math.sqrt(sumOfSquares / (totalComments - 1)));

			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: MedianStdDevDriver <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf,
				"StackOverflow Comment Length Median StdDev By Hour");
		job.setJarByClass(SmarterMedianStdDevDriver.class);
		job.setMapperClass(SOMedianStdDevMapper.class);
		job.setCombinerClass(SOMedianStdDevCombiner.class);
		job.setReducerClass(SOMedianStdDevReducer.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(SortedMapWritable.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(MedianStdDevTuple.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static class MedianStdDevTuple implements Writable {
		private float median = 0;
		private float stddev = 0f;

		public float getMedian() {
			return median;
		}

		public void setMedian(float median) {
			this.median = median;
		}

		public float getStdDev() {
			return stddev;
		}

		public void setStdDev(float stddev) {
			this.stddev = stddev;
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			median = in.readFloat();
			stddev = in.readFloat();
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeFloat(median);
			out.writeFloat(stddev);
		}

		@Override
		public String toString() {
			return median + "\t" + stddev;
		}
	}
}
