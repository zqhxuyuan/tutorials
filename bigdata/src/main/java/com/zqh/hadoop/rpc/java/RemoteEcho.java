package com.zqh.hadoop.rpc.java;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RemoteEcho implements Echo{
    public String echo(String echo) {
        return "from remote:"+echo;
    }
}