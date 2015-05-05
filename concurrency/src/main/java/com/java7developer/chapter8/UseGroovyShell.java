package com.java7developer.chapter8;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.math.BigDecimal;

/**
 * Code for listing 8_10
 */
public class UseGroovyShell {

  public static void main(String[] args) {
    Binding binding = new Binding();
    binding.setVariable("x", 2.4);
    binding.setVariable("y", 8);
    GroovyShell shell = new GroovyShell(binding);
    Object value = shell.evaluate("x + y");
    assert value.equals(new BigDecimal(10.4));
  }

}