package com.zqh.crunch;

import org.apache.crunch.Pipeline;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.impl.spark.SparkPipeline;
import org.apache.hadoop.conf.Configuration;
//import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by hadoop on 15-1-14.
 */
public class CrunchUtil {

    public Pipeline getMRPipeline(Configuration conf){
        return new MRPipeline(CrunchUtil.class, conf);
    }

    public Pipeline getMemPipeline(){
        return MemPipeline.getInstance();
    }

    /*
    public Pipeline getSparkPipeline(JavaSparkContext sparkContext){
        return new SparkPipeline(sparkContext, "");
    }
    */
}
