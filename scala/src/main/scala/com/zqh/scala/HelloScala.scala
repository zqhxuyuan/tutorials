package com.zqh.scala

/**
 * Created by hadoop on 15-2-4.
 */
object HelloScala {

  def main(args : Array[String]){
    println("hello scala...")

    //testArray
    testFlatMap
  }

  /**
  [25]Vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
  [25]Vector(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49)
  [25]Vector(50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74)
  [25]Vector(75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99)
   */
  def testArray(){
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

    val possib
    case class CTest(v: Int)
    val s = Set(Map(CTest(0) -> List(0, 3), CTest(1) -> List(0, 2)))ilities = s flatMap { m =>
      val mapping = m flatMap {
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
}
