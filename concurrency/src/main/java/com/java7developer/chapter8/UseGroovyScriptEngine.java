package com.java7developer.chapter8;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.IOException;

/**
 * Code for listing 8_12
 */
public class UseGroovyScriptEngine {
  public static void main(String[] args) {
    try {
      String[] roots = new String[] { "/src/main/groovy" };
      GroovyScriptEngine gse = new GroovyScriptEngine(roots);

      Binding binding = new Binding();
      binding.setVariable("name", "Gweneth");

      Object output = gse.run("Hello.groovy", binding);
      assert output.equals("Hello Gweneth");
    } catch (IOException | ResourceException | ScriptException e) {
      System.out.println(e.getMessage());
    }
  }
}