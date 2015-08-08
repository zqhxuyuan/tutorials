package com.zqh.calcite;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by zqhxuyuan on 15-7-12.
 */
public class HelloCalcite {
    public static void main(String[] args) throws Exception{
        Connection connection = calciteDriver();
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);

        //Official Code is wrong
        //ReflectiveSchema.create(calciteConnection, calciteConnection.getRootSchema(), "hr", new HrSchema());

        //First Try failed
        //SchemaFactory fc = new ReflectiveSchema.Factory();
        //fc.create(calciteConnection.getRootSchema(),"hr",null);

        //See If any test code offered to us
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        rootSchema.add("hr", new ReflectiveSchema(new HrSchema()));

        Statement statement = calciteConnection.createStatement();
        //having count(*)表示按照deptno分组,出现次数大于1的, 但是模拟的数据每个分组只有一条数据,为什么还会返回结果??
        ResultSet resultSet = statement.executeQuery(
                "select d.deptno, min(e.empid) empid "
                        + "from hr.emps as e "
                        + "join hr.depts as d "
                        + "on e.deptno = d.deptno "
                        + "group by d.deptno "
                        + "having count(*) > 1");  //照理说,应该不会有结果
        while(resultSet.next()){
            int deptno = resultSet.getInt("deptno");
            int minEmp = resultSet.getInt("empid");
            System.out.println(deptno + "->" + minEmp);
        }

        resultSet.close();
        statement.close();
        connection.close();
    }

    public static Connection calciteDriver() throws Exception{
        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        return connection;
    }

    public static void mysql(CalciteConnection calciteConnection) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        final String CATALOG = null;
        JdbcSchema.create(calciteConnection.getRootSchema(), "name", dataSource, CATALOG, "hr");
    }

    public static class HrSchema {
        public final Employee[] emps = {
                new Employee(100, "Bill",1),
                new Employee(200, "Eric",1),
                new Employee(150, "Sebastian",3),
        };

        public final Department[] depts = {
                new Department(1, "LeaderShip"),
                new Department(2, "TestGroup"),
                new Department(3, "Development")
        };
    }

    public static class Employee {
        public final int empid;
        public final String name;
        public final int deptno;

        public Employee(int empid, String name, int deptno) {
            this.empid = empid;
            this.name = name;
            this.deptno = deptno;
        }
    }

    public static class Department{
        public final int deptno;
        public final String name;

        public Department(int deptno, String name){
            this.deptno = deptno;
            this.name = name;
        }
    }
}
