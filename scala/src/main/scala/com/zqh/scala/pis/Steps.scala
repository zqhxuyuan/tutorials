package com.zqh.scala.pis

/**
 * Created by hadoop on 15-2-14.
 *
 * http://www.artima.com/scalazine/articles/steps.html
 */
object Steps extends App{

  // Step 4. Define some methods
  def max(x: Int, y: Int): Int = if (x < y) y else x
  def max2(x: Int, y: Int) = if (x < y) y else x
  def max3(x: Int, y: Int) = { if (x < y) y else x }
  max(3, 5)

  def greet() = println("Hello, world!")
  greet()
  greet

  // Step 6. Loop with while, decide with if
  var i = 0
  while (i < args.length) {
    if (i != 0)
      print(" ")
    print(args(i))
    i += 1
  }

  // Step 8. Parameterize Arrays with types
  val greetStrings = new Array[String](3)
  greetStrings(0) = "Hello"
  greetStrings(1) = ", "
  greetStrings(2) = "world!\n"
  for (i <- 0 to 2)
    print(greetStrings(i))

  // Step 9. Use Lists and Tuples
  val oneTwoThree = List(1, 2, 3)
  val oneTwo = List(1, 2)
  val threeFour = List(3, 4)
  val oneTwoThreeFour = oneTwo ::: threeFour
  println(oneTwo + " and " + threeFour + " were not mutated.")
  println("Thus, " + oneTwoThreeFour + " is a new List.")

  val twoThree = List(2, 3)
  val oneTwoThree2 = 1 :: twoThree
  println(oneTwoThree2)

  val oneTwoThree3 = 1 :: 2 :: 3 :: Nil
  println(oneTwoThree)

  val thrill = "Will" :: "fill" :: "until" :: Nil
  thrill(2)
  thrill.count(s => s.length == 4)
  thrill.drop(2)
  thrill.dropRight(2)
  thrill.exists(s => s == "until")
  thrill.filter(s => s.length == 4)
  thrill.forall(s => s.endsWith("l"))
  thrill.foreach(s => print(s))
  thrill.foreach(print)
  thrill.head
  thrill.init
  thrill.tail
  thrill.last
  thrill.isEmpty
  thrill.length
  thrill.map(s => s + "y")
  //thrill.remove(s => s.length == 4)
  thrill.reverse
  //thrill.sort((s, t) => s.charAt(0).toLowerCase < t.charAt(0).toLowerCase)

  val pair = (99, "Luftballons")
  println(pair._1)
  println(pair._2)


  // Step 10. Use Sets and Maps
  import scala.collection.mutable.HashSet

  val jetSet = new HashSet[String]
  jetSet += "Lear"
  jetSet += ("Boeing", "Airbus")
  println(jetSet.contains("Cessna"))


  import scala.collection.mutable.HashMap

  val treasureMap = new HashMap[Int, String]
  treasureMap += 1 -> "Go to island."
  treasureMap += 2 -> "Find big X on ground."
  treasureMap += 3 -> "Dig."
  println(treasureMap(2))

  val romanNumeral = Map(1 -> "I", 2 -> "II", 3 -> "III", 4 -> "IV", 5 -> "V")
  println(romanNumeral(4))

  // Step 11. Understand classes and singleton objects
  class SimpleGreeter {
    val greeting = "Hello, world!"
    def greet() = println(greeting)
  }
  val g = new SimpleGreeter
  g.greet()


  class FancyGreeter(greeting: String) {
    def greet() = println(greeting)
  }
  val g0 = new FancyGreeter("Salutations, world")
  g0.greet


  class CarefulGreeter(greeting: String) {
    if (greeting == null) {
      throw new NullPointerException("greeting was null")
    }
    def greet() = println(greeting)
  }
  //new CarefulGreeter(null)


  class RepeatGreeter(greeting: String, count: Int) {
    def this(greeting: String) = this(greeting, 1)
    def greet() = {
      for (i <- 1 to count)
        println(greeting)
    }
  }
  val g1 = new RepeatGreeter("Hello, world", 3)
  g1.greet()
  val g2 = new RepeatGreeter("Hi there!")
  g2.greet()


  class WorldlyGreeter(greeting: String) {
    def greet() = {
      val worldlyGreeting = WorldlyGreeter.worldify(greeting)
      println(worldlyGreeting)
    }
  }
  // The WorldlyGreeter companion object
  object WorldlyGreeter {
    def worldify(s: String) = s + ", world!"
  }
  WorldlyGreeter.worldify("hello")
  val wg = new WorldlyGreeter("Hello")
  wg.greet()


  // Step 12. Understand traits and mixins
  trait Friendly {
    def greet() = "Hi"
  }

  class Dog extends Friendly {
    override def greet() = "Woof"
  }

  class HungryCat extends Friendly {
    override def greet() = "Meow"
  }

  class HungryDog extends Dog {
    override def greet() = "I'd like to eat my own dog food"
  }

  trait ExclamatoryGreeter extends Friendly {
    override def greet() = super.greet() + "!"
  }

  var pet: Friendly = new Dog
  println(pet.greet())

  pet = new HungryCat
  println(pet.greet())

  pet = new HungryDog
  println(pet.greet())

  pet = new Dog with ExclamatoryGreeter
  println(pet.greet())

  pet = new HungryCat with ExclamatoryGreeter
  println(pet.greet())

  pet = new HungryDog with ExclamatoryGreeter
  println(pet.greet())
}
