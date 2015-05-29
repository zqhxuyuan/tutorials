package com.zqh.scala.stm;

/**
 * Created by hadoop on 15-2-26.
 */
public class TestException extends RuntimeException {
    public TestException() {
        super("Expected failure");
    }
}
