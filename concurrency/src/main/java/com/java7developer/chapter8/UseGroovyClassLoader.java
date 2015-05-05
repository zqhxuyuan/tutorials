package com.java7developer.chapter8;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Code for listing 8_11
 */
public class UseGroovyClassLoader {

  public static void main(String[] args) {
    GroovyClassLoader loader = new GroovyClassLoader();

    try {
      Class<?> groovyClass = loader.parseClass(new File("CalculateMax.groovy"));

      GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

      ArrayList<Integer> numbers = new ArrayList<>();
      numbers.add(new Integer(1));
      numbers.add(new Integer(10));
      Object[] arguments = { numbers };

      Object value = groovyObject.invokeMethod("getMax", arguments);
      assert value.equals(new Integer(10));
    } catch (CompilationFailedException | IOException | InstantiationException
        | IllegalAccessException e) {
      System.out.println(e.getMessage());
    }
  }
}