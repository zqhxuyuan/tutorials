package com.zqh.nosql.riak;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.operations.FetchBucketPropsOperation;
import com.basho.riak.client.core.query.BucketProperties;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hadoop on 15-1-8.
 */
public class RiakUtil {

    public static RiakClient simpleClient() throws Exception{
        return RiakClient.newClient("localhost");
    }

    public static RiakClient getLocalClient() throws Exception{
        RiakNode.Builder builder = new RiakNode.Builder();
        builder.withMinConnections(10);
        builder.withMaxConnections(50);

        List<String> addresses = new LinkedList<String>();
        //addresses.add("192.168.1.1");
        //addresses.add("192.168.1.2");
        //addresses.add("192.168.1.3");
        addresses.add("localhost");

        List<RiakNode> nodes = RiakNode.Builder.buildNodes(builder, addresses);
        RiakCluster cluster = new RiakCluster.Builder(nodes).build();
        cluster.start();
        RiakClient client = new RiakClient(cluster);

        return client;
    }

    public static void closeCluster(RiakCluster cluster){
        cluster.start();
        // There is also a method to shut the cluster down:
        cluster.shutdown();
    }

    public static void printProperties(Namespace ns) throws Exception{
        FetchBucketPropsOperation fetchProps = new FetchBucketPropsOperation.Builder(ns).build();
        BucketProperties p = fetchProps.get().getBucketProperties();
        System.out.println("AllowMulti:"+p.getAllowMulti());
        System.out.println("Quorum:"+p.getBasicQuorum());
        System.out.println("DW:" + p.getDw().getIntValue());
    }

    public Location createLocation(String bucketType, String bucketName){
        // In the Java client (and all clients), if you do not specify a bucket type, the client will use the default type.
        // And so the following store command would be equivalent to the one above:
        //Location johnSmithKey = new Location(new Namespace("default", "users"), "john_smith");
        return new Location(new Namespace(bucketType), bucketName);
    }
}
