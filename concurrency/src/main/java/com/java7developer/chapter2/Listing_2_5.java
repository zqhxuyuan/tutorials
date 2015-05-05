package com.java7developer.chapter2;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import static java.nio.file.attribute.PosixFilePermission.*;

/**
 * Code for Listing 2_5 - You'll need to change the hardcoded path.
 */
public class Listing_2_5 {

  public static void main(String[] args) {
    try {
      Path profile = Paths.get("/user/Admin/.profile");

      PosixFileAttributes attrs = Files.readAttributes(profile,
          PosixFileAttributes.class);

      Set<PosixFilePermission> posixPermissions = attrs.permissions();
      posixPermissions.clear();

      String owner = attrs.owner().getName();
      String perms = PosixFilePermissions.toString(posixPermissions);
      System.out.format("%s %s%n", owner, perms);

      posixPermissions.add(OWNER_READ);
      posixPermissions.add(GROUP_READ);
      posixPermissions.add(OWNER_READ);
      posixPermissions.add(OWNER_WRITE);
      Files.setPosixFilePermissions(profile, posixPermissions);

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
