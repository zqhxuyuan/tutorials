package com.java7developer.chapter2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Code for Listing 2_4 - You'll need to change the hardcoded path.
 */
public class Listing_2_4 {

  public static void main(String[] args) {
    try {
      Path zip = Paths.get("/usr/bin/zip");
      System.out.println(zip.toAbsolutePath().toString());
      System.out.println(Files.getLastModifiedTime(zip));
      System.out.println(Files.size(zip));
      System.out.println(Files.isSymbolicLink(zip));
      System.out.println(Files.isDirectory(zip));
      System.out.println(Files.readAttributes(zip, "*"));
    } catch (IOException ex) {
      System.out.println("Exception" + ex.getMessage());
    }
  }
}
