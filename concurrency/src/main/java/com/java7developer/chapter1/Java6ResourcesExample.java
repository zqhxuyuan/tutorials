package com.java7developer.chapter1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Java6ResourcesExample {

  private void run() {
    File file = new File("foo");
    URL url = null;
    try {
      url = new URL("http://www.google.com/");
    } catch (MalformedURLException e) {
    }

    InputStream is = null;
    try {
      is = url.openStream();
      OutputStream out = new FileOutputStream(file);
      try {
        byte[] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) >= 0)
          out.write(buf, 0, len);
      } catch (IOException iox) {
        // Handle IOException from the read or wrtie
      } finally {
        try {
          out.close();
        } catch (IOException closeOutx) {
          // Handle a possible IOException from close() of out
        }
      }
    } catch (FileNotFoundException fnfx) {
      // Handle FileNotFoundException from openStream()
    } catch (IOException openx) {
      // Handle other IOException from openStream()
    } finally {
      try {
        if (is != null)
          is.close();
      } catch (IOException closeInx) {
        // Handle a possible IOException from close() of is
      }
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Java6ResourcesExample instance = new Java6ResourcesExample();
    instance.run();
  }

}
