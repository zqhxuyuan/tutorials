package com.zqh.nosql.mongodb;

import com.mongodb.*;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by hadoop on 15-2-3.
 */
public class HelloMongo {

    public static void main(String[] args) throws Exception{
        String username = "aipayFileDB";
        String password = "123456";
        String host = "172.17.212.73";
        String database = "aipayFileDB";

        MongoClient mongoClient = new MongoClient(host, 27017);
        //MongoCredential credential = MongoCredential.createMongoCRCredential(username, database, password.toCharArray());
        //MongoClient mongoClient = new MongoClient(new ServerAddress(host), Arrays.asList(credential));

        // 1.数据库列表
//        for (String s : mongoClient.getDatabaseNames()) {
//            System.out.println("DatabaseName=" + s);
//        }

        // 2.链接student数据库: use aipayFileDB
        DB db = mongoClient.getDB("aipayFileDB");
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);

        // 3.用户验证: db.auth('aipayFileDB','123456')
        boolean auth = db.authenticate(username, password.toCharArray());
        System.out.println("auth=" + auth);

        // 4.集合列表: show collections
        Set<String> colls = db.getCollectionNames();
        for (String s : colls) {
            System.out.println("CollectionName=" + s);
        }

        // 5.获取摸个集合对象
        DBCollection coll = db.getCollection("fs.files");
        System.out.println(coll.count());
    }
}
