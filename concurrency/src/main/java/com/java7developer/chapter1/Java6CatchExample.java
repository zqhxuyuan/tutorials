package com.java7developer.chapter1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

public class Java6CatchExample {

  public static class Configuration {
  }

  public static class ConfigurationException extends Exception {
  }

  public Configuration getConfig(String fileName) {
    Configuration cfg = null;
    try {
      String fileText = getFile(fileName);
      cfg = verifyConfig(parseConfig(fileText));
    } catch (FileNotFoundException fnfx) {
      System.err.println("Config file '" + fileName + "' is missing");
    } catch (IOException e) {
      System.err.println("Error while processing file '" + fileName + "'");
    } catch (ConfigurationException e) {
      System.err.println("Config file '" + fileName + "' is not consistent");
    } catch (ParseException e) {
      System.err.println("Config file '" + fileName + "' is malformed");
    }

    return cfg;
  }

  private Configuration verifyConfig(Configuration parseConfig)
      throws ConfigurationException {
    // This is just here to ensure code compiles
    return null;
  }

  // throws ParseException if file is malformed
  private Configuration parseConfig(String fileText) throws ParseException {
    // This is just here to ensure code compiles
    return null;
  }

  // Can throw a FileNotFoundException if file doesn't exist, or IOException if
  // something bad happens
  // while trying to read from it
  private String getFile(String fileName_) throws IOException {
    // This is just here to ensure code compiles
    return null;
  }
}
