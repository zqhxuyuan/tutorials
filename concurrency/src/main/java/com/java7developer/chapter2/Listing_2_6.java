package com.java7developer.chapter2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Code for Listing 2_6 - You'll need to change the hardcoded path.
 */
public class Listing_2_6 {

  public static void main(String[] args) {

    Path file = Paths.get("/opt/platform/java");
    try {
      if (Files.isSymbolicLink(file)) {
        file = Files.readSymbolicLink(file);
      }
      Files.readAttributes(file, BasicFileAttributes.class);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
