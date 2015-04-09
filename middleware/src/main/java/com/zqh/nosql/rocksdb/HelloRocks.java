package com.zqh.nosql.rocksdb;

import org.rocksdb.RocksDB;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;

/**
 * Created by zqhxuyuan on 15-3-4.
 *
 * https://github.com/facebook/rocksdb/wiki/RocksJava-Basics
 */
public class HelloRocks {

    public static void main(String[] args) throws RocksDBException{
        // a static method that loads the RocksDB C++ library.
        RocksDB.loadLibrary();
        // the Options class contains a set of configurable DB options
        // that determines the behavior of a database.
        Options options = new Options().setCreateIfMissing(true);
        // a factory method that returns a RocksDB instance
        RocksDB db = RocksDB.open(options, "/home/hadoop/data/rocksdb");

        byte[] key1 = "HelloRocks".getBytes();
        byte[] key2 = "RocksDB".getBytes();

        db.put(key1, "RocksDBValue".getBytes());

        // some initialization for key1 and key2
        byte[] value = db.get(key1);
        if (value != null) {  // value == null if key1 does not exist in db.
            db.put(key2, value);
        }
        db.remove(key1);

        System.out.println(db.get(key2));

        if (db != null) db.close();
        options.dispose();
    }
}
