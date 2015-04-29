package com.zqh.hadoop.rpc;

import org.apache.hadoop.ipc.ProtocolSignature;

import java.io.IOException;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class ClientProtocolImpl implements ClientProtocol {
    @Override
    public String echo(String echo) throws IOException {
        System.out.printf("Server:"+echo);
        return echo;
    }

    @Override
    public long getProtocolVersion(String s, long l) throws IOException {
        return ClientProtocol.versionID;
    }

    @Override
    public ProtocolSignature getProtocolSignature(String s, long l, int i) throws IOException {
        return new ProtocolSignature(ClientProtocol.versionID, null);
    }
}
