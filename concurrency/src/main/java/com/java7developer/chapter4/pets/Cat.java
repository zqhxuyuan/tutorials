package com.java7developer.chapter4.pets;

public class Cat extends Pet {
  public Cat(String name) {
    super(name);
  }

  public void examine() {
    System.out.println("Meow!");
  }
}