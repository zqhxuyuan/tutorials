package com.zqh.hive;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

/**
 * Created by zqhxuyuan on 15-3-10.
 *
 * 1.   start-dfs.sh
 * [2]. start-yarn.sh
 * 3.   hiveserver2
 */
public class HiveJDBCTest {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //UserName: 操作系统的用户名, 不是hive meta-store的用户名
        //为什么是操作系统的用户名呢? 因为在创建hdfs的/usr/hive/warehouse目录时是用当前操作系统的用户执行hadoop fs -mkdir创建的
        //创建后数据仓库目录的用户就是操作系统的当前用户.要建表也是在这个目录下的数据库(默认是default)下新建文件夹的
        Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "zhengqh", "");
        Statement stmt = con.createStatement();
        String tableName = "jdbcTest";
        stmt.execute("drop table if exists " + tableName);
        stmt.execute("create table " + tableName + " (key int, value string)");
        System.out.println("Create table success!");

        // show tables
        String sql = "show tables '" + tableName + "'";
        System.out.println("Running: " + sql);
        ResultSet res = stmt.executeQuery(sql);
        if (res.next()) {
            System.out.println(res.getString(1));
        }

        // describe table
        sql = "describe " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(res.getString(1) + "\t" + res.getString(2));
        }

        sql = "select * from " + tableName;
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
        }

        sql = "select count(1) from " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while (res.next()) {
            System.out.println(res.getString(1));
        }
    }
}
