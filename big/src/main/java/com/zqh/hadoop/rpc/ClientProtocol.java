package com.zqh.hadoop.rpc;

import org.apache.hadoop.ipc.VersionedProtocol;

import java.io.IOException;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public interface ClientProtocol extends VersionedProtocol{

    public static final long versionID = 1L;

    String echo(String echo) throws IOException;
}
