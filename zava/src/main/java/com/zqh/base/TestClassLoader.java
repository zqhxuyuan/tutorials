package com.zqh.base;

/**
 * Created by zhengqh on 15/9/18.
 */
public class TestClassLoader {

    public static void main(String[] args) throws Exception{
        testPath();
    }

    public static void testPath() throws Exception{
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println(path);
    }
}
