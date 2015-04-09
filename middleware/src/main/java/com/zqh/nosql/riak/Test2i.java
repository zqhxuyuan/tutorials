package com.zqh.nosql.riak;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.LongIntIndex;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.basho.riak.client.core.util.BinaryValue;

import java.util.List;

/**
 * Created by hadoop on 15-1-8.
 *
 * http://docs.basho.com/riak/latest/dev/using/2i/
 */
public class Test2i {

    public static void main(String[] args) throws Exception{
        RiakClient client = RiakUtil.getLocalClient();
        Location johnSmithKey = new Location(new Namespace("users"), "john_smith");

        // 1. Inserting the Object with Secondary Indexes
        RiakObject obj = new RiakObject()
                .setContentType("application/json")
                .setValue(BinaryValue.create("{'user_data':{ ... }}"));

        obj.getIndexes().getIndex(StringBinIndex.named("twitter")).add("jsmith123");
        obj.getIndexes().getIndex(StringBinIndex.named("email")).add("jsmith@basho.com");

        StoreValue store = new StoreValue.Builder(obj)
                .withLocation(johnSmithKey)
                .build();
        client.execute(store);


        // 2. Querying the Object with Secondary Indexes
        Namespace usersBucket = new Namespace("users");
        BinIndexQuery biq = new BinIndexQuery.Builder(usersBucket, "twitter", "jsmith123")
                .build();
        BinIndexQuery.Response response = client.execute(biq);
        List<BinIndexQuery.Response.Entry> entries = response.getEntries();
        for (BinIndexQuery.Response.Entry entry : entries) {
            System.out.println(entry.getRiakObjectLocation().getKey());
        }


        // 3. Indexing Objects
        Namespace peopleBucket = new Namespace("indexes", "people");

        RiakObject larry = new RiakObject()
                .setValue(BinaryValue.create("My name is Larry"));
        larry.getIndexes().getIndex(StringBinIndex.named("field1")).add("val1");
        larry.getIndexes().getIndex(LongIntIndex.named("field2")).add(1001L);
        StoreValue storeLarry = new StoreValue.Builder(larry)
                //.withLocation(peopleBucket.setKey("larry"))
                .withLocation(new Location(peopleBucket, "larry"))
                .build();
        client.execute(storeLarry);


        RiakObject moe = new RiakObject()
                .setValue(BinaryValue.create("Ny name is Moe"));
        moe.getIndexes().getIndex(StringBinIndex.named("Field1")).add("val2");
        moe.getIndexes().getIndex(LongIntIndex.named("Field2")).add(1002L);
        StoreValue storeMoe = new StoreValue.Builder(moe)
                //.withLocation(peopleBucket.setKey("moe"))
                .withLocation(new Location(peopleBucket, "moe"))
                .build();
        client.execute(storeMoe);

        RiakObject curly = new RiakObject()
                .setValue(BinaryValue.create("My name is Curly"));
        curly.getIndexes().getIndex(StringBinIndex.named("FIELD1")).add("val3");
        curly.getIndexes().getIndex(LongIntIndex.named("FIELD2")).add(1003L);
        StoreValue storeCurly = new StoreValue.Builder(curly)
                //.withLocation(peopleBucket.setKey("curly"))
                .withLocation(new Location(peopleBucket, "curly"))
                .build();
        client.execute(storeCurly);

        RiakObject veronica = new RiakObject()
                .setValue(BinaryValue.create("My name is Veronica"));
        veronica.getIndexes().getIndex(StringBinIndex.named("field1"))
                .add("val4").add("val4");
        veronica.getIndexes().getIndex(LongIntIndex.named("field2"))
                .add(1004L).add(1005L).add(1006L).add(1004L).add(1004L).add(1007L);

    }
}
