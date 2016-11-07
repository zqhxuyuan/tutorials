package com.zqh.guice.anno;

//import javax.inject.Named;
//@Named("Good")
public class GoodPlayer implements Player{

    public void bat() {
        System.out.println("I can hit any ball");
    }

    public void bowl() {
        System.out.println("I can also bowl");
    }
}