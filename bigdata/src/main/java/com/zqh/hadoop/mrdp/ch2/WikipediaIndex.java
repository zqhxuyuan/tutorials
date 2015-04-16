package com.zqh.hadoop.mrdp.ch2;

import java.io.IOException;
import java.util.Map;

import com.zqh.hadoop.mrdp.MRDPUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Chapter2 Pattern2:Inverted Index Summarizations
 *
 Suppose we want to add StackOverflow links to each Wikipedia page that is referenced in a StackOverflow comment.
 添加这样的StackOverflow的链接: 在StackOverflow的Comment中含有指向Wikipedia的链接

 The following example analyzes each comment in Stack‐Overflow to find hyperlinks to Wikipedia.
 If there is one, the link is output with the comment ID to generate the inverted index.
 如果存在这样的链接, 会用CommentId用来生成倒排索引

 When it comes to the reduce phase, all the comment IDs that reference the same hyperlink will be grouped together.
 在Reduce阶段,所有指向同一个Wikipedia的链接的CommentId会被分在同一组
 **所以我们的Key是wikipedia链接, Value是所有指向key[wikipedia链接]的StackOverflow的Comment的ID**

 These groups are then concatenated together into a white space delimited String and directly output to the file system.
 From here, this data file can be used to update the Wikipedia page with all the comments that reference it.
 这样当更新Wikipedia链接时, 所有指向该Wikipedia链接的Comment的引用也都更新

 如果不是倒排索引, 则Key是Comment, 而Value是该Comment的所有Wikipedia链接.
 当更新某个Wikipedia链接时, 则要遍历所有的Comment,
 对每个Comment里的Value里的所有Wikipedia链接进行再次遍历, 如果等于要更新的Wikipedia, 则更新Value里对应元素的值

 Problem: Given a set of user’s comments, build an inverted index of Wikipedia URLs to a set of answer post IDs .
 */
public class WikipediaIndex {

    // 获取Comment中的Wikipedia URL, 如果一个Comment有多个Wikipedia呢?
	public static String getWikipediaURL(String text) {

        // 检查Comment中是否包含wikipedia链接.
		int idx = text.indexOf("\"http://en.wikipedia.org");
		if (idx == -1) return null;

        int idx_end = text.indexOf('"', idx + 1);
		if (idx_end == -1) return null;

		int idx_hash = text.indexOf('#', idx + 1);

		if (idx_hash != -1 && idx_hash < idx_end) {
			return text.substring(idx + 1, idx_hash);
		} else {
			return text.substring(idx + 1, idx_end);
		}

	}

    /**
     * parses the posts from StackOverflow to output the row IDs
     * of all answer posts that contain a particular Wikipedia URL
     */
	public static class SOWikipediaExtractor extends Mapper<Object, Text, Text, Text> {

		private Text link = new Text();
		private Text outkey = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			// Parse the input string into a nice map
			Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());

			// Grab the necessary XML attributes
			String txt = parsed.get("Body");
			String posttype = parsed.get("PostTypeId");
			String row_id = parsed.get("Id");

			// if the body is null, or the post is a question (1), skip
			if (txt == null || (posttype != null && posttype.equals("1"))) {
				return;
			}

            // txt是StackOverflow的Comment
			// Unescape the HTML because the SO data is escaped.
			txt = StringEscapeUtils.unescapeHtml(txt.toLowerCase());

            // Wikipedia的URL作为key, Comment的ID作为value, 这正是我们需要的!
			link.set(getWikipediaURL(txt));
			outkey.set(row_id);
			context.write(link, outkey);
		}
	}

	public static class Concatenator extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			StringBuilder sb = new StringBuilder();
			for (Text id : values) {
				sb.append(id.toString() + " ");
			}

			result.set(sb.substring(0, sb.length() - 1).toString());

            // Key是WikipediaURL, value是所有含有该URL的CommentID
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: WikipediallIndex <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "StackOverflow Wikipedia URL Inverted Index");
		job.setJarByClass(WikipediaIndex.class);
		job.setMapperClass(SOWikipediaExtractor.class);
		job.setCombinerClass(Concatenator.class);
		job.setReducerClass(Concatenator.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
