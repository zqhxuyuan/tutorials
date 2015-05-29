package com.zqh.spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

/**
 * No Need to start others, just run it as Scala Application on IDEA. you should setMaster, or else will get
 * Caused by: org.apache.spark.SparkException: A master URL must be set in your configuration at ...
 */
object SimpleApp {

  val conf = new SparkConf().setAppName("Simple Application").setMaster("local")
  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    simpleapp
  }

  // ** Simpel Application **
  def simpleapp{
    val logFile = "file:/home/hadoop/data/README.md"
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }

}