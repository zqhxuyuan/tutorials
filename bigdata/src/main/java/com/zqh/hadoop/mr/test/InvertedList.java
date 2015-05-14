package com.zqh.hadoop.mr.test;

import org.apache.commons.lang.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zqhxuyuan on 15-4-24.
 *
 * input format
 *    docid<tab>doc content
 *
 * output format
 *    (term:docid)<tab>(tf in this doc)
 *
 */
public class InvertedList{


    public class InvertedListMap extends Mapper<IntWritable/*docid*/, Text/*doc content*/, Text, IntWritable> {

        /**
         *
         * @param key docId
         * @param value document
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(IntWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            //termFreqs: 单词在document中出现的次数
            HashMap<Text, IntWritable> freqs = new HashMap<Text, IntWritable> ();

            // the document can be preprocessed by third analyzed tool first
            // here is simplify this procedure using split by whitespace instead
            String[] terms = value.toString().split(" ");
            for (String term : terms ) {
                if (term == null || "".equals(term)) continue;

                if (freqs.containsKey(new Text(term))) {
                    //已经出现过, 次数+1
                    int tf = freqs.get(new Text(term)).get();
                    freqs.put(new Text(term), new IntWritable(tf + 1));
                } else {
                    //还没出现过, 初始化次数=1
                    freqs.put(new Text(term), new IntWritable(1));
                }
            } // end of for loop

            Iterator<Map.Entry<Text, IntWritable>> entryIter = (Iterator<Map.Entry<Text, IntWritable>>) freqs.entrySet().iterator();
            while (entryIter.hasNext()) {
                Map.Entry<Text, IntWritable> entry = entryIter.next();
                Text tuple = new Text();
                //term:docId
                tuple.set(entry.getKey().toString() + ":" + key.toString());
                //termFreqs
                context.write(tuple, freqs.get(entry.getKey()));
            }
        }

    }

    public class InvertedListReduce extends Reducer <Text, IntWritable, Text, Map<IntWritable, IntWritable>> {

        private String term = null;
        private Map<IntWritable, IntWritable> posting = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            term = null;
            posting = new TreeMap<IntWritable, IntWritable>();
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            context.write(new Text(term), posting);
        }

        /**
         * 一个文档相同的term会被分为多个Map处理. 不同的Map最终到同一个Reduce的key是相同的term:docId.
         * values代表term在对应的Map中出现的次数
         *
         * D1: A B C A 分成2个Map处理[A B]和[C A]最终结果:
         * Map1:
         *   A:D1 1
         *   B:D1 1
         * Map2:
         *   C:D1 1
         *   A:D1 1
         *
         * 到Reduce阶段的2个Map其中
         *   A:D1 1  [Map1]
         *   A:D1 1  [Map2]
         * 都会到同一个Reduce中
         *
         * Reduce处理A:D1这个key时, values=[1 1]
         *
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //key = term:docId
            String[] tuple = key.toString().split(":");
            if (term != null && !term.equalsIgnoreCase(tuple[0])) {
                context.write(new Text(tuple[0]), posting);
                posting.clear();
            } else {
                //when term is null, it's the first time receive a term:docId
                for (IntWritable val : values) {
                    //docId, termFreqs
                    posting.put(new IntWritable(Integer.parseInt(tuple[1])), val);
                }

                //set term from current key. so that if statement will executed
                term = key.toString();
            }
        }

    }
}