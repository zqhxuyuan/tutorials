package com.zqh.scala.pis

/**
 * Created by zqhxuyuan on 15-2-28.
 */
class ch12_trait {

  val frog = new Frog
  frog.philosophize()

  val phil: Philosophical = frog
  phil.philosophize()


  val queue = new BasicIntQueue
  queue.put(10)
  queue.put(20)
  queue.get()
  queue.get()

  val queue2 = new MyQueue
  queue2.put(10)
  queue2.get()

  val queue3 = new BasicIntQueue with Doubling
  queue3.put(10)
  queue3.get()

  val queue4 = (new BasicIntQueue with Incrementing with Filtering)
  queue4.put(-1); queue.put(0); queue.put(1)
  queue4.get()
  queue4.get()

  val queue5 = (new BasicIntQueue with Filtering with Incrementing)
  queue4.put(1); queue.put(0); queue.put(1)
  queue4.get()
  queue4.get()
  queue4.get()
}

trait Philosophical {
  def philosophize() {
    println("I consume memory, therefore I am!")
  }
}

class Frog extends Philosophical {
  override def toString = "green"

  override def philosophize() {
    println("It ain't easy being "+ toString +"!")
  }
}

class Animal
class Frog2 extends Animal with Philosophical {
  override def toString = "green"
}

trait HasLegs
class Frog3 extends Animal with Philosophical with HasLegs {
  override def toString = "green"
}

trait CharSequence {
  def charAt(index: Int): Char
  def length: Int
  def subSequence(start: Int, end: Int): CharSequence
  def toString(): String
}

abstract class IntQueue {
  def get(): Int
  def put(x: Int)
}

import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put(x: Int) { buf += x }
}

trait Doubling extends IntQueue {
  abstract override def put(x: Int) { super.put(2 * x) }
}

class MyQueue extends BasicIntQueue with Doubling


trait Incrementing extends IntQueue {
  abstract override def put(x: Int) { super.put(x + 1) }
}

trait Filtering extends IntQueue {
  abstract override def put(x: Int) {
    if (x >= 0) super.put(x)
  }
}
