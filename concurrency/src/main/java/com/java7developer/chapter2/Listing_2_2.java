package com.java7developer.chapter2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Code for Listing 2_2 - You'll need to change the hardcoded path.
 */
public class Listing_2_2 {

  public static void main(String[] args) {

    Path dir = Paths.get("C:\\workspace\\java7developer");

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
        "*.properties")) {
      for (Path entry : stream) {
        System.out.println(entry.getFileName());
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
