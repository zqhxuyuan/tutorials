package com.zqh.akka.essentials.stm.pingpong

import akka.actor.ActorSystem

import scala.concurrent.stm.Ref

object Game {
  def main(args: Array[String]): Unit = {
    val _system = ActorSystem("Ref-example")
    val turn = Ref(new String())
    val table = new PingPong(turn)

    val alice = new Thread(new Player("bob", table))
    val bob = new Thread(new Player("alice", table))

    alice.setName("alice")
    bob.setName("bob")
    alice.start()             // alice starts playing
    bob.start()               // bob starts playing
    try {
      Thread.sleep(500)       // Wait .5 seconds
    } catch {
      case _ : Throwable =>   // eat the exception
    }
    table.hit("DONE")         // cause the players to quit their threads.
    try {
      Thread.sleep(1000)
    } catch {
      case _ : Throwable =>   // eat the exception
    }
    _system.shutdown
  }


  class PingPong(whoseTurn: Ref[String]) {

    def hit(opponent: String): Boolean = {
      val x: String = Thread.currentThread().getName

      if (whoseTurn.single.get == "") {
        whoseTurn.single.set(x)
        return true
      } else if (whoseTurn.single.get.compareTo(x) == 0) {
        println("PING! (" + x + ")")
        whoseTurn.single.set(opponent)
        return true
      } else {
        try {
          val t1 = System.currentTimeMillis()
          wait(2500)
          if ((System.currentTimeMillis() - t1) > 2500) {
            println("****** TIMEOUT! " + x
              + " is waiting for " + whoseTurn + " to play.")
          }
        } catch {
          case _ : Throwable =>
        }
      }
      if (opponent.compareTo("DONE") == 0) {
        whoseTurn.single.set(opponent)
        return false
      }
      if (whoseTurn.single.get.compareTo("DONE") == 0) {
        return false
      }
      return true // keep playing.
    }
  }

  class Player(myOpponent: String, myTable: PingPong) extends Runnable {
    override def run(): Unit = {
      while (myTable.hit(myOpponent)) {}
    }
  }
}