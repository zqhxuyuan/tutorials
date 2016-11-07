package com.zqh.hadoop.mrdp.ch3;

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

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by zqhxuyuan on 15-5-19.
 */
public class HBaseBloomFilteringDriver {

    public static class BloomFilteringMapper extends Mapper<Object, Text, Text, NullWritable> {
        private BloomFilter filter = new BloomFilter();
        private HTable table = null;

        protected void setup(Context context) throws IOException, InterruptedException {
            // Get file from the Distributed Cache
            URI[] files = DistributedCache.getCacheFiles(context.getConfiguration());
            System.out.println("Reading Bloom filter from: " + files[0].getPath());

            // Open local file for read.
            DataInputStream strm = new DataInputStream(new FileInputStream(files[0].getPath()));
            // Read into our Bloom filter.
            filter.readFields(strm);
            strm.close();

            // Get HBase table of user info
            Configuration hconf = HBaseConfiguration.create();
            table = new HTable(hconf, "user_table");
        }

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> parsed = MRDPUtils.transformXmlToMap(value.toString());
            // Get the value for the comment
            String userid = parsed.get("UserId");

            // If this user ID is in the set
            if (filter.membershipTest(new Key(userid.getBytes()))) {
                // Get the reputation from the HBase table
                Result r = table.get(new Get(userid.getBytes()));
                int reputation = Integer.parseInt(new String(r.getValue("attr".getBytes(), "Reputation".getBytes())));
                // If the reputation is at least 1500, write the record to the file system
                if (reputation >= 1500) {
                    context.write(value, NullWritable.get());
                }
            }
        }
    }
}
