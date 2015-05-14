package com.zqh.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-5-6.
 *
 * -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 */
public class HeapOOM {

    static class OOMObject{}

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();

        while(true){
            list.add(new OOMObject());
        }
    }
}
