package com.zqh.crunch.wc;


import java.io.Serializable;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.PCollection;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.Pipeline;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.types.writable.Writables;

/**
 * @author fabio
 * this class does top word count without a separate class for the parallel do. Indeed it requires that the
 * class implements the serializable class
 */
public class TopWordCount extends Configured implements Tool, Serializable{

    @Override
    public int run(String[] args) throws Exception {


        //use the mem pipeline to test MemPipeline.getInstance()
        Pipeline pipeline = new MRPipeline(TopWordCount.class,getConf());
        // Reference a given text file as a collection of Strings.
        PCollection<String> lines = pipeline.readTextFile(args[0]);

        //funzione che splitta ogni linea in parole
        PCollection<String> words = lines.parallelDo(new DoFn<String, String>() {

            @Override
            public void process(String input, Emitter<String> emitter) {
                for (String word : input.split("\\s+")) {
                    emitter.emit(word);
                }
            }
        }, Writables.strings());

        PTable<String, Long> out = words.count();

        for ( Pair<String, Long> wordCount: words.count().top(20).materialize()) {
            System.out.println(wordCount);
        }

        pipeline.writeTextFile(out, args[1]);

        return  pipeline.done().succeeded() ? 0: 1;
    }

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new TopWordCount(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
