package com.zqh.scala

import net.sf.json.JSONObject

/**
 * Created by zhengqh on 15/8/11.
 */
object BaseTest {

  def testCaseCond(): Unit ={
    val num = 1
    num match {
      case n if n>0 => print("OK")
      case _ => print("NO")
    }
  }

  def testCompare(): Unit ={
    def compareGenericType[T <: Ordered[T]](t1 : T, t2: T): Boolean ={
      /*
      var flag = false
      if(t1.isInstanceOf[Integer]){
        val v1 : Integer = t1.asInstanceOf[Integer]
        val v2 : Integer = t1.asInstanceOf[Integer]
        flag = v1 > v2
      }else if(t1.isInstanceOf[String]){
        val v1 : String = t1.asInstanceOf[String]
        val v2 : String = t1.asInstanceOf[String]
        flag = v1 > v2
      }
      flag
      */
      t1 > t2
    }
    def compare2[T](a:T, b:T)(implicit ordering:Ordering[T]) = {
      import ordering._;
      a > b
    }
    def max[T](a:T, b:T)(implicit ordering:Ordering[T]) = {
      import ordering._;
      if(a>b) a else b
    }

    //inferred type arguments [Int] do not conform to method compareGenericType's type parameter bounds [T <: Ordered[T]]
    //compareGenericType(1,2)
    max(1,2)
    max("abc","ced")
    compare2(1,2)
    compare2("abc","cef")
  }

  //数据类型, Any测试
  def testDataType(): Unit ={
    //Any类型测试
    val kvMap = scala.collection.mutable.Map[String, Any]()
    kvMap += "1" -> 1.123
    kvMap += "2" -> 2.0
    kvMap += "3" -> "3String"

    //scala.collection.mutable.Iterable[Class[_]] = ArrayBuffer(class java.lang.Double, class java.lang.Double, class java.lang.String)
    val arrStr = kvMap.map(m=>{
      m._2.getClass.toString
      /*
      m._2.getClass match{
        case Int.getClass => "INT"
        case Long.getClass => "LONG"
        case Double.getClass => "DOUBLE"
        case _ => "..."
      }
      */
    })
    //scala.collection.mutable.Iterable[String] = ArrayBuffer(Double, Double, String)
    arrStr.map(e=>{
      e match{
        case "class java.lang.Double" => "Double"
        case "class java.lang.String" => "String"
      }
    })

    //枚举类
    def testEnum: Unit = {
      object DIMENSION extends Enumeration {
        type DIMENSION = Value
        val ACCOUNT = Value(0, "accountLogin")
        val IPADDR = Value(1, "trueipAddress")
        val DEVICE = Value(2, "deviceId")
        val MOBILE = Value(3, "accountMobile")
      }

      val dimension2 = List(DIMENSION.ACCOUNT, DIMENSION.IPADDR, DIMENSION.DEVICE, DIMENSION.MOBILE)
      dimension2.foreach(dim => {
        //打印Value中的id和名称.
        println(dim.id)
        println(dim.toString)
        //case匹配
        dim match {
          case DIMENSION.ACCOUNT => {
          }
          case _ =>
        }
      })

      def doWithEnum(dim: DIMENSION.Value): Unit = {
        println(dim.id)
        println(dim.toString)
      }

      doWithEnum(DIMENSION.ACCOUNT)
    }
  }

  def testJsonParse(): Unit ={
    val jsonStr =
      """
        |{
        |              "accountEmail": "zhangsan@163.com",
        |              "accountLogin": "zhangsan",
        |              "accountMobile": "13957108801",
        |              "accountPhone": "051788662981",
        |              "create": 1400233173196,
        |              "deviceId": "8c6bedcc872185296e074cc881fa1d99",
        |              "eventId": "download_click",
        |              "ipAddress": "122.224.126.12",
        |              "payAmount": 0,
        |              "smartId": "203fff3e435725984e42c61499e1d1be",
        |              "status": "ACC"
        |            }
      """.stripMargin
    val json = JSONObject.fromObject(jsonStr)
    println(json.get("accountLogin"))
  }

  def testTuple(): Unit ={
    def toTuple[A <: Object](as:List[A]):Product = {
      val tupleClass = Class.forName("scala.Tuple" + as.size)
      tupleClass.getConstructors.apply(0).newInstance(as:_*).asInstanceOf[Product]
    }

    val t1 = toTuple(List("hello", "world"))
    val t2 = toTuple(List("hello", "world", "scala"))

    t1.isInstanceOf[Tuple2[String,String]]
    t1 == ("hello","world")
  }

  def testSelfType(): Unit ={
    val vec3_1 = new Vec3(2.0, 4.0, 8.0)
    val vec3_2 = new Vec3(1.0, 2.0, 3.0)
    val subVec3 = vec3_1 - vec3_2
    println(subVec3)
  }

  def main (args: Array[String]): Unit = {
    testSelfType()
  }
}

//stackoverflow.com/questions/4776864/scala-self-type-value-is-not-a-member-error
trait Vec[V <: Vec[V]] { self: V =>
  def -(v:V): V
  def /(d:Double): V
  def dot(v:V): Double

  def norm:Double = math.sqrt(this dot this)
  def normalize: V = self / norm
  def dist(v: V) = (self - v).norm
  def nasty(v: V) = (self / norm).norm
}
class Vec3(val x:Double, val y:Double, val z:Double) extends Vec[Vec3] {
  def -(d:Vec3) = new Vec3(x-d.x, y-d.y, z-d.z)
  def /(d:Double) = new Vec3(x / d, y / d, z / d)
  def dot(v:Vec3) = x * v.x + y * v.y + z * v.z
  def cross(v:Vec3):Vec3 = {
    val (a, b, c) = (v.x, v.y, v.z)
    new Vec3(c * y - b * z, a * z - c * x, b * x - a * y)
  }
  def perpTo(v:Vec3) = (this.normalize).cross(v.normalize)
  override def toString() = x + "," + y + "," + z
}