package com.java7developer.chapter4;

public class Author {

  private final String name;

  public String getName() {
    return name;
  }

  public Author(String name_) {
    name = name_;
  }

  @Override
  public String toString() {
    return "Author [name=" + name + "]";
  }

}
