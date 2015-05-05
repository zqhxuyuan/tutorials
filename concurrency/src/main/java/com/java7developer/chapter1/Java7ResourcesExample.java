package com.java7developer.chapter1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Java7ResourcesExample {

  private void run() throws IOException {
    File file = new File("foo");
    URL url = null;
    try {
      url = new URL("http://www.google.com/");
    } catch (MalformedURLException e) {
    }

    try (OutputStream out = new FileOutputStream(file);
        InputStream is = url.openStream()) {
      byte[] buf = new byte[4096];
      int len;
      while ((len = is.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Java7ResourcesExample instance = new Java7ResourcesExample();
    instance.run();
  }

}
