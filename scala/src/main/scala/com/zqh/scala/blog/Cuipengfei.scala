package com.zqh.scala.blog

/**
 * Created by hadoop on 15-2-27.
 *
 * http://cuipengfei.me/blog/archives/
 */
object Cuipengfei extends App{

  // 4. Trait
  new Rose().makePeopleHappy
  new Dog().makePeopleHappy

  // 5. Type Alias
  case class Person(age : Int, income : Double)
  type People = List[Person]

  def teenagers(people: People): People = {
    people.filter(person => person.age < 20)
  }

  type PersonPredicate = Person => Boolean
  val teenagerPred: PersonPredicate = person => person.age < 20

  def teenagers2(people: People): People = {
    people.filter(teenagerPred)
  }

  type Tax = Person => Double
  val incomeTax: Tax = person => person.income * 5 / 100
  val kejuanzaTax: Tax = person => person.income * 20 / 100

  def giveMeYourMoney(p: Person) = {
    calculateTax(p, List(incomeTax, kejuanzaTax))
  }

  def calculateTax(person: Person, taxes: List[Tax]): Double = {
    taxes.foldLeft(0d) {
      (acc, curTax) => acc + curTax(person)
    }
  }

  type TwoToOne = (String, Int) => Double
  def twoToOneImpl: TwoToOne = (str, i) => 1

  type NoInJustOut = () => String
  def noInJustOutImpl: NoInJustOut = () => "hello world"

  // 6. Partial Application
  def greet(greeting: String, name: String) = greeting + " " + name
  def sayHello = greet("hello", _: String)
  def greetXiaoMing = greet(_: String, "Xiao Ming")

  sayHello("world")
  greetXiaoMing("Ni Hao")

  def greet2(greeting: String)(name: String) = greeting + " " + name
  def sayHello2 = greet2("hello")(_)
  def greetXiaoMing2 = greet2(_: String)("Xiao Ming")

  // 7. Apply
  class Water {
    var isWarm : Boolean = false
  }
  class Kettle {
    def apply(water: Water) = {
      water.isWarm = true
      water
    }
  }
  val kettle: Kettle = new Kettle()
  kettle(new Water())

  object Kettle {
    def apply(water: Water) = {
      water.isWarm = true
      water
    }
  }
  Kettle(new Water())

  List(1, 2, 3)
  List("a", "b", "c")
  Map(1 -> "a", 2 -> "b", 3 -> "c")

  // 8. Pattern Matching
  val number = 1
  val result = number match {
    case 1 => 1
    case 2 => 2
    case _ => null
  }

  abstract class Animal
  case class Cat(name: String) extends Animal
  case class Dgg(name: String) extends Animal

  val animal = createAnimal
  animal match {
    case Dgg(abc) => println("this is a dog")
    case Cat(cde) => println("this is a cat named kitty")
    case _ => println("other animal")
  }

  //Don't return Cat or Dgg!
  def createAnimal : Animal = {
    Dgg("Miao")
  }

  val hostPort = ("localhost", 80)
  hostPort match {
    case ("localhost", port) => "this is localhost address"
    case (host, port) => "some other address"
  }

  val map = Map(1 -> "one", 2 -> "two")
  map.get(1) match {
    case Some(str) => println("get something from map: " + str)
    case None => "no result"
  }

  // 9. Function Composition
  def sayHi(name: String) = "Hi, " + name
  def sayBye(str: String) = str + ", bye"
  val names = List("world", "tom", "xiao ming")
  names.map(sayHi).map(sayBye)//.foreach(println)

  names.map {
    name => sayBye(sayHi(name))//.foreach(print)
  }//.foreach(println)

  names.map(sayHi _ andThen sayBye).foreach(println)

  // 10. Implicit Function
  class Duck {
    def makeDuckNoise() = "gua gua"
  }
  class Chicken {
    def makeChickenNoise() = "ge ge"
  }
  class Ducken(chicken: Chicken) extends Duck {
    override def makeDuckNoise() = chicken.makeChickenNoise()
  }

  def giveMeADuck(duck: Duck) = duck.makeDuckNoise()

  giveMeADuck(new Duck)
  giveMeADuck(new Ducken(new Chicken))

  implicit def chickenToDuck(chicken: Chicken) = new Ducken(chicken)
  giveMeADuck(new Chicken)

  // 11. Structural Types
  // makeNoise方法的参数必须有一个叫做quack的方法，该quack方法返回值类型为String
  def makeNoise(quacker: {def quack(): String}) = quacker.quack
  class Duck2 {
    def quack() = "real quack"
  }
  makeNoise(new Duck2)
  class NotADuck {
    def quack() = "fake quack"
  }
  makeNoise(new NotADuck)
  makeNoise(new {
    def quack() = "anonymous quack"
  })

  // 12. more-on-scala-implicit-function
  class APerson(name: String) {
    def eat(food: String) = println("I just ate " + food)
  }
  new APerson("Xiao ming").eat("steamed dumplings")
  new APerson("Xiao ming") eat "boiled dumplings"

  implicit def stringToPerson(name: String) = new APerson(name)

  "Xiao ming" eat "more dumplings"

  // 13. Default Parameter Value
  def hello(name: String = "world") = println("hello " + name)
  //hello
  hello()
  hello("everybody")

  class Greeter {
    def hello(name: String = "world") = println("hello " + name)
  }
  class AnotherClass {
    new Greeter().hello()
  }

  // 14. Update Method
  val scores = new scala.collection.mutable.HashMap[String, Int]
  scores("Bob") = 100
  val bobsScore = scores("Bob")

  val scores2 = new scala.collection.mutable.HashMap[String, Int]
  scores2.update("Bob", 100)
  val bobsScore2 = scores2.apply("Bob")

  class AddressChanger {
    def update(name: String, age: Int, newAddress: String) = {
      println(s"changing address of $name, whose age is $age to $newAddress")
      //actually change the address
    }
  }

  val changer = new AddressChanger()
  changer.update("xiao ming", 23, "beijing")

  val addressOf = new AddressChanger()
  //addressOf(name = "xiao ming", age = 23) = "beijing"
  addressOf("xiao ming",23) = "beijing"

  // 15. unapply,unapplySeq
  object Square {
    def unapply(z: Double): Option[Double] = Some(math.sqrt(z))
  }
  val anumber: Double = 36.0
  Square.unapply(anumber)

  val bnumber: Double = 36.0
  bnumber match {
    case Square(n) => println(s"square root of $bnumber is $n")
    case _ => println("nothing matched")
  }

  object Names {
    def unapplySeq(str: String): Option[Seq[String]] = {
      if (str.contains(",")) Some(str.split(","))
      else None
    }
  }

  val namesString = "xiao ming,xiao hong,tom"
  namesString match {
    case Names(first, second, third) => {
      println("the string contains three people's names")
      println(s"$first $second $third")
    }
    case _ => println("nothing matched")
  }

  // 16. Lower Bound
  // 定义Pair的类，其中可以包含两个元素，元素类型为泛型的T
  // 把第二个元素second和一个新的元素newFirst结合起来组成一个新的Pair。
  // 新的元素的类型是泛型的R。新组成的Pair的类型是Pair[R]。
  // [R >: T]。这种标记的含义是说R是T的基类/父类。那么一个T和一个R自然可以组合成一个R的Pair了。
  class Pair[T](val first: T, val second: T) {
    def replaceFirst[R >: T](newFirst: R): Pair[R] = new Pair[R](newFirst, second)
  }
  class Vehicle {}
  class Car extends Vehicle {}
  class Tank extends Vehicle {}

  // T: Car, R: Vehicle ==> Vehicle >: Car
  val twoCars: Pair[Car] = new Pair(new Car(), new Car())
  val tankAndCar: Pair[Vehicle] = twoCars.replaceFirst(new Tank())

  // 17. Option & for
  for(i<-1 to 10;j<-1 to 10;k<-1 to 10) yield(s"$i $j $k")

  var myMap : Map[String, Int] = Map("price" -> 1, "amount" -> 2)
  
  def calculateTotal: Option[Int] = {
    val price: Option[Int] = getValue(myMap, "price")
    val amount: Option[Int] = getValue(myMap, "amount")

    if (price.isEmpty || amount.isEmpty) {
      None
    } else {
      Some(price.get * amount.get)
    }
  }

  def getValue(myMap: Map[String, Int], key : String): Option[Int] ={
    myMap.get(key)
  }
  def getPrice(): Option[Int] ={
    myMap.get("price")
  }
  def getAmount(): Option[Int] ={
    myMap.get("amount")
  }

  def calculateTotalWithFor: Option[Int] = {
    for (price <- getPrice; amount <- getAmount) yield price * amount
  }
}

// 1. Object
object HowIsObjectImplementedInScala {
  def printSomething() {
    println("printSomething")
  }
}

//Var&Val
class HowAreVarAndValImplementedInScala {
  var v1 = 123
  val v2 = 456

  def method1() {
    var v3 = 123
    val v4 = 456
    println(v3 + v4)
  }
}

// Constructor
class ScalaConstructorExample(val x: Double, y: String) {
  println(x + y)

  def this(x: Double) = {
    this(x, "hello")
  }
}

//Trait
abstract class Plant {
  def photosynthesis = println("Oh, the sunlight!")
}
class Ruderal extends Plant {
  def grow = println("I take up all the space!")
}

abstract class Animal {
  def move = println("I can move!")
}
class Snake extends Animal {
  def bite = println("I am poisonous!")
}

trait PeoplePleaser {
  def makePeopleHappy = println("People like me")
}

class Rose extends Plant with PeoplePleaser {
  def smell = println("Good!")
}

class Dog extends Animal with PeoplePleaser {
  def bark = println("Woof!")
}

