package example

/**
 * Created by zqhxuyuan on 15-7-22.
 */
object CollectionTest {
  //-----------------------------------------
  //List,Map

  def listMap(): Unit = {
    val rddDemo = List(Map("sid" -> 1, "payCnt" -> 1, "payMin" -> 20, "paySum" -> 100),
      Map("sid" -> 2, "payCnt" -> 2, "payMin" -> 10, "paySum" -> 150),
      Map("sid" -> 3, "payCnt" -> 1, "payMin" -> 25, "paySum" -> 125)
    )
    val metrixKeys = List("payCnt", "payMin", "paySum")
    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        r.getOrElse("payCnt", 0), //写死了,能不能根据keys的个数,自动设置
        r.getOrElse("payMin", 0),
        r.getOrElse("paySum", 0)
        )
    })
    //List[(Int, Int, Int, Int)] = List((1,1,20,100), (2,2,10,150), (3,1,25,125))

    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        })
        )
    })
    //List[(Int, List[Int])] = List((1,List(1, 20, 100)), (2,List(2, 10, 150)), (3,List(1, 25, 125)))

    //如何将上面的(1,List(1,20,100))转换为扁平结构的(1,1,20,100)
    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        }) //.flatten 不能在这里加
        match {
          case List(a, b, c) => (a, b, c) // 参数固定住,不好
        }
        )
    })
    //List[(Int, List[Int])] = List((1,(1, 20, 100)), (2,(2, 10, 150)), (3,(1, 25, 125)))

    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        })
        match {
          case List(a, b, c) => a + "," + b + "," + c //勉强看起来有点像,但是参数也是固定的
        }
        )
    })
    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        })
        match {
          // 直接利用上面表达式的返回值作为case的类型,并指定一个变量,最终的返回值用变量计算: 比如转成字符串
          case list: List[Int] => list.mkString(",")
        }
        )
    })
    //List[(Int, String)] = List((1,1,20,100), (2,2,10,150), (3,1,25,125))

    rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        })
        )
    }) match {
      // list里面的每个元素是Tuple,访问其中的某个用下标,比如_1表示Int, _2表示内层的List[Int]
      case list: List[(Int, List[Int])] => {
        list.map(e => {
          e._1 + "," + e._2.mkString(",")
        })
      }
    }
    //List[String] = List(1,1,20,100, 2,2,10,150, 3,1,25,125)
    //现在不同行之间无法区分了. 我们想要的结果应该是List[(String)] = List((1,1,20,100), (2,2,10,150), (3,1,25,125))
    //分析下上面的类型为什么是List[String]
    //因为对list进行map操作, e就是list的每个元素, {}中是对每个元素的计算. {}中的结果类型就是计算过后的每个元素的类型, 这里是String,所以结果list是List[String]
    //那么如何表示成List[(String)]的呢? 给上面的加上(), 不行!

    val table = rddDemo.map(r => {
      (r.getOrElse("sid", 0),
        metrixKeys.map(k => {
          r.getOrElse(k, 0)
        })
        )
    }) match {
      case list: List[(Int, List[Int])] => {
        list.map(e => {
          List(e._1 + "," + e._2.mkString(","))
        })
      }
    }
    //List[List[String]] = List(List(1,1,20,100), List(2,2,10,150), List(3,1,25,125))
    //我们可以把整个List看做是一张表, 其中的每一个List则是每一行数据.

    table.foreach(row => {
      row.foreach(col => {
        print(col + ",")
      })
      println()
    })

    /**
    1,1,20,100,
    2,2,10,150,
    3,1,25,125,
      */
  }

  //----------------------------------------
  def testListOp(): Unit = {
    //初始化数组,并指定个数
    var l = Array[Int](5) // 这里的5不是个数,而是值
    l = new Array[Int](5) // 用new可以创建5个元素. THIS IS COOL!
    l = Array.fill[Int](5)(0) // 或者用Array的静态方法

    val l1 = scala.collection.mutable.MutableList(1, 2, 3)
    val l2 = scala.collection.mutable.MutableList(2, 4, 5)
    l1 ++ l2 //两个list相加
    //1::l1     //list添加元素, 不可变List
    l1 += 1 //可变List添加一个元素, 添加到尾部
    l1.+=:(2) //添加到列表头部


    //终端打印的不是完整的字符串,可以进入:power模式,设置vals.isettings.maxPrintString = 10000
    val cntKeys = List("payeeUserid", "payeeIdNumber", "payeeCardNumber", "trueipAddressCity", "deviceId", "trueipAddress", "accountName", "accountMobile", "accountAddressStreet", "mobileAddressCity")
    val colsInSelect = cntKeys.map(key => s"size(collect_set($key) OVER " +
      s"(PARTITION BY accountLogin ORDER BY eventOccurTime range BETWEEN 59200 PRECEDING AND CURRENT ROW)) $key").mkString(",\n")

    val colsKey = cntKeys.map(key => "k:" + key)
  }

  /**
  [25]Vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
  [25]Vector(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49)
  [25]Vector(50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74)
  [25]Vector(75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)
    */
  def testArrayOp(){
    var dataArray = Range(0, 100)
    var sumSize = 100
    var numWorkers = 4
    var jobPerWork = sumSize / numWorkers
    for(i <- 1 to numWorkers){ // 1, 2, 3, 4
    /*
    var endIndex = 0
    if (i == numWorkers){
      endIndex = sumSize - 1
    }else{
      endIndex = i*jobPerWork
    }
    */
    var endIndex = i*jobPerWork

      val sendData = dataArray.slice((i-1)*jobPerWork,endIndex)
      println("["+sendData.size + "]" + sendData)
    }
  }


  //http://stackoverflow.com/questions/20215518/scala-map-flatten-and-flatmap-not-equivalent
  def testFlatMap(){

    case class CTest(v: Int)
    val s = Set(Map(CTest(0) -> List(0, 3), CTest(1) -> List(0, 2)))
    val possibilities = s flatMap { m =>
      val mapping = m.toIterable.flatMap {
        case (label, destNodes) => destNodes map {
          case nodes => (label, nodes) }
      }
      mapping
    }
    //Set((CTest(0),3), (CTest(1),2))
    println(possibilities)

    val possibilities2 = s flatMap { m =>
      val mapping = m map {
        case (label, destNodes) => destNodes map {
          case nodes => (label, nodes) }
      }
      mapping.flatten
    }
    //Set((CTest(0),0), (CTest(0),3), (CTest(1),0), (CTest(1),2))
    println(possibilities2)

    val possibilities3 = s flatMap { m =>
      val mapping = m.toIterable.flatMap {
        case (label, destNodes) => destNodes map {
          case nodes => (label, nodes) }
      }
      mapping
    }
    //Set((CTest(0),0), (CTest(0),3), (CTest(1),0), (CTest(1),2))
    println(possibilities3)
  }

  def testForReturn(): Unit ={
    val forRetNull = for(i <- 1 to 10){
      ("index:"+i, i)
    }
    val forRet = (1 to 10).map(i=>{
      ("index:"+i, i)
    })
    val forYield = for(i <- 1 to 10) yield{
      ("index:"+i, i)
    }
    //IndexedSeq[IndexedSeq[(String, Int)]]
    val doubleForYield = for(i <- 1 to 10; z <- 1 to 2) yield{
      for(j <- 1 to 5) yield {
        ("index:"+i+"-"+j, i*j)
      }
    }
    doubleForYield.flatMap(x=>x) //IndexedSeq[(String, Int)]
  }

  //删除数组元素
  implicit class Foo[T](as: Array[T]) {
    def dropping(i: Int) = as.view.take(i) ++ as.view.drop(i+1)
  }
}