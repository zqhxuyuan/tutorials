package com.zqh.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hadoop on 14-12-8.
 */
public class HelloWorld {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My App");
        JavaSparkContext sc = new JavaSparkContext(conf);

        List<Integer> listData = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> listRDD = sc.parallelize(listData);
        System.out.println(listRDD.count());

        JavaRDD<String> distFile = sc.textFile("helloworld/data/helloworld.txt");
        JavaRDD<String> lines = sc.textFile("helloworld/data/helloworld.txt");

        JavaRDD<Integer> lineLengths = lines.map(new Function<String, Integer>() {
            public Integer call(String s) { return s.length(); }
        });
        int totalLength = lineLengths.reduce(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer a, Integer b) { return a + b; }
        });

        System.out.println(lineLengths.count());
    }
}
