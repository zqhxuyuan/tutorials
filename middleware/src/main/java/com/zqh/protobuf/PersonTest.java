package com.zqh.protobuf;

import java.io.*;

/**
 * Created by zqhxuyuan on 15-7-11.
 */
public class PersonTest {

    public static void main(String[] args) throws Exception{
        PersonProtos.Person.Builder personBuilder = PersonProtos.Person.newBuilder();
        personBuilder.setEmail("test@gmail.com");
        personBuilder.setId(1000);
        PersonProtos.Person.PhoneNumber.Builder phone = PersonProtos.Person.PhoneNumber.newBuilder();
        phone.setNumber("18610000000");

        personBuilder.setName("张三");
        personBuilder.addPhone(phone);

        PersonProtos.Person person = personBuilder.build();

        //第一种方式
        //序列化
        byte[] data = person.toByteArray();//获取字节数组，适用于SOCKET或者保存在磁盘。
        //反序列化
        PersonProtos.Person result = PersonProtos.Person.parseFrom(data);
        System.out.println(result.getEmail());

        //第二种序列化：粘包,
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //生成一个由：[字节长度][字节数据]组成的package。特别适合RPC场景
        person.writeDelimitedTo(byteArrayOutputStream);
        //反序列化
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        result = PersonProtos.Person.parseDelimitedFrom(byteArrayInputStream);
        System.out.println(result.getEmail());

        //第三种序列化,写入文件或者Socket
        FileOutputStream fileOutputStream = new FileOutputStream(new File("/test.dt"));
        person.writeTo(fileOutputStream);
        fileOutputStream.close();

        FileInputStream fileInputStream = new FileInputStream(new File("/test.dt"));
        result = PersonProtos.Person.parseFrom(fileInputStream);
        System.out.println(result);
    }
}
