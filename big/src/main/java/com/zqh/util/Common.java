package com.zqh.util;

/**
 * Created by hadoop on 15-1-22.
 */
public class Common {

    public static String filePath(String file){
        return "/home/hadoop/IdeaProjects/go-bigdata/helloworld/" + file;
    }

    public static String hdfsUrl(){
        return "hdfs://localhost:9000/";
    }
}
