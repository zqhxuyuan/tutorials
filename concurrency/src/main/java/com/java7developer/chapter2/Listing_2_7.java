package com.java7developer.chapter2;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Code for Listing 2_7 - You'll need to change the hardcoded path.
 */
public class Listing_2_7 {

  /*
   * shutdown is provided as a static boolean set to false for compile purposes.
   * In real world case shutdown could/would be set to true by a client
   */
  private static boolean shutdown = false;

  public static void main(String[] args) {

    try {
      WatchService watcher = FileSystems.getDefault().newWatchService();

      Path dir = FileSystems.getDefault().getPath("/usr/karianna");

      WatchKey key = dir.register(watcher, ENTRY_MODIFY);

      while (!shutdown) {
        key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {
          if (event.kind() == ENTRY_MODIFY) {
            System.out.println("Home dir changed!");
          }
        }
        key.reset();
      }
    } catch (IOException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }
}
