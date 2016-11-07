package com.zqh.hadoop.mr.coocurrance;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * User: Bill Bejeck
 * Date: 11/27/12
 * Time: 10:33 PM
 */
public class PairsRelativeOccurrenceReducer extends Reducer<WordPair, IntWritable, WordPair, DoubleWritable> {
    private DoubleWritable totalCount = new DoubleWritable();
    private DoubleWritable relativeCount = new DoubleWritable();
    private Text currentWord = new Text("NOT_SET");
    private Text flag = new Text("*");

    @Override
    protected void reduce(WordPair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        System.out.println("KKK:"+key+"|VVV:"+printIterator(values));
        if (key.getNeighbor().equals(flag)) {
            if (key.getWord().equals(currentWord)) {
                totalCount.set(totalCount.get() + getTotalCount(values));
                System.out.println("QQQQQ");
            } else {
                currentWord.set(key.getWord());
                totalCount.set(0);
                totalCount.set(getTotalCount(values));
            }
        } else {
            int count = getTotalCount(values);
            if(totalCount.get() == 0){
                //System.out.println("XXXXX");
            }
            relativeCount.set((double) count / totalCount.get());
            context.write(key, relativeCount);
        }
    }

    private int getTotalCount(Iterable<IntWritable> values) {
        int count = 0;
        for (IntWritable value : values) {
            count += value.get();
        }
        //System.out.print("      TOTALCOUNT:" + count);
        return count;
    }

    public String printIterator(Iterable<IntWritable> values){
        StringBuffer sb = new StringBuffer();
        for (IntWritable value : values) {
            sb.append(""+value.get()+",");
        }
        return sb.toString();
    }
}
