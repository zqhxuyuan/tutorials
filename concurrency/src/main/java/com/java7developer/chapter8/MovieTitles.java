package com.java7developer.chapter8;

import java.util.*;

/**
 * Code for listing 8_7
 */
public class MovieTitles {
  public static void main(String[] args) {
    List<String> movieTitles = new ArrayList<>();
    movieTitles.add("Seven");
    movieTitles.add("Snow White");
    movieTitles.add("Die Hard");

    for (String movieTitle : movieTitles) {
      System.out.println(movieTitle);
    }
  }
}