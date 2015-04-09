package com.zqh.nosql.riak;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.RegisterUpdate;
import com.basho.riak.client.api.commands.datatypes.UpdateMap;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

/**
 * Created by hadoop on 15-1-7.
 *
 * https://github.com/basho/riak-java-client
 *
 * http://riak.com.cn/riak/latest/theory/concepts/
 *
 * sudo riak start
 *
 */
public class HelloWorld {

    public static void main(String[] args) throws Exception{
        RiakClient client = RiakUtil.getLocalClient();

        // Get Data In
        // Namespace如果使用2个参数,分别是bucketType, bucketName, 默认的bucketType=default
        // Bucket 和键 是在 Riak 中租出数据的唯一方式。数据通过“bucket/键”组合来存储和识别。
        Namespace ns = new Namespace("default", "my_bucket");
        RiakUtil.printProperties(ns);
        // key = DB's rowkey, so bucket = DB's table. default bucketType = default db
        Location location = new Location(ns, "my_key");

        // value is a RiakObject
        RiakObject riakObject = new RiakObject();
        BinaryValue binaryValue = BinaryValue.create("my_value");
        riakObject.setValue(binaryValue);

        // How do we associate value to key
        StoreValue store = new StoreValue.Builder(riakObject)
                .withLocation(location)
                .withOption(StoreValue.Option.W, new Quorum(3)).build();
        // save <Namespace.Location, RiakObject>=<row-key, value> to db
        client.execute(store);

        // Get Data Out
        // 如果客户端知道 bucket 和键，就可以通过 API 直接获取 Riak 中存储的对象。
        // get by key: location is a key which associate namespace info
        FetchValue fv = new FetchValue.Builder(location).build();
        // execute query operation, and return query result
        FetchValue.Response response = client.execute(fv);
        // the result is RiakObject, just same as when we save it.
        RiakObject obj = response.getValue(RiakObject.class);
        BinaryValue outBinaryValue = obj.getValue();

        // In and Out, doesn't change
        assert binaryValue == outBinaryValue;
        RiakUtil.printProperties(ns);

    }

    /**
     * A bucket type must be created (in all local and remote clusters) before 2.0 data types can be used.
     * In the example below, it is assumed that the type "my_map_type" has been created
     * and associated to the "my_map_bucket" prior to this code executing.
     * @param client
     * @throws Exception
     */
    public void testMap(RiakClient client) throws Exception{
        // 因为bucketType默认是default字符串, 如果声明了另外的类型, 则需要事先创建!
        Namespace ns = new Namespace("my_map_type", "my_map_bucket");
        Location location = new Location(ns, "my_key");

        RegisterUpdate ru1 = new RegisterUpdate(BinaryValue.create("map_value_1"));
        RegisterUpdate ru2 = new RegisterUpdate(BinaryValue.create("map_value_2"));

        MapUpdate mu = new MapUpdate();
        mu.update("map_key_1", ru1);
        mu.update("map_key_2", ru2);

        UpdateMap update = new UpdateMap.Builder(location, mu).build();
        client.execute(update);
    }
}
