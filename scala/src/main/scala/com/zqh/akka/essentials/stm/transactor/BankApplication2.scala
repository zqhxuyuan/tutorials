package com.zqh.akka.essentials.stm.transactor

import akka.actor.SupervisorStrategy.{Escalate, Stop, Resume}
import akka.actor._
import akka.transactor.{Coordinated, CoordinatedTransactionException, Transactor}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.stm.Ref

/**
 * Created by hadoop on 15-2-27.
 */
object BankApplication2 {
  val system = ActorSystem("STM-Example")
  implicit val timeout = Timeout(5 seconds)
  val bank = system.actorOf(Props[BankActor], name = "BankActor")

  def main(args: Array[String]): Unit = {
    showBalances()
    bank ! new TransferMsg(1500)
    showBalances()
    bank ! new TransferMsg(1400)
    showBalances()
    bank ! new TransferMsg(3500)
    showBalances()
    system.shutdown()
  }

  case class AccountBalance(accountNumber: String, accountBalance: Float)
  case class AccountCredit(amount: Float)
  case class AccountDebit(amount: Float)
  case class TransferMsg(amtToBeTransferred: Float)

  def showBalances(): Unit = {
    Thread.sleep(2000)
    bank ! new AccountBalance("XYZ", 0)
    bank ! new AccountBalance("ABC", 0)
  }


  class AccountActor(accountNumber: String, inBalance: Float) extends Transactor {

    val balance = Ref(inBalance)

    //atomatically, 原子操作, 可以同时接收普通msg和coordinated msg
    //对于coordinated msg对象, 会把自己加入到该transaction中去
    //对于普通msg对象, 直接起单独的transaction运行
    def atomically = implicit txn => {
      case message: AccountDebit =>
        if (balance.single.get < message.amount)
          throw new IllegalStateException("Insufficient Balance")
        else
          balance transform (_ - message.amount)
      case message: AccountCredit =>
        balance transform (_ + message.amount)
    }

    //普通操作, 无论收到什么msg, 都完全bypass coordinated transactions
    override def normally: Receive = {
      case value: AccountBalance =>
        sender ! new AccountBalance(accountNumber, balance.single.get)
    }

  }

  class BankActor extends Actor with ActorLogging {

    val transferActor = context.actorOf(Props[TransferActor], name = "TransferActor")
    implicit val timeout = Timeout(5 seconds)

    def receive = {
      case transfer: TransferMsg =>
        transferActor ! transfer
      case balance: AccountBalance =>
        val future = (transferActor ? balance).mapTo[AccountBalance]
        val account = Await.result(future, timeout.duration)
        println("Account #" + account.accountNumber + " , Balance #" + account.accountBalance)
    }

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
      case _: CoordinatedTransactionException => Resume
      case _: IllegalStateException => Resume
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }
  }


  class TransferActor extends Actor {

    val fromAccount = "XYZ"
    val toAccount = "ABC"

    val from = context.actorOf(Props(new AccountActor(fromAccount, 5000)), name = fromAccount)
    val to = context.actorOf(Props(new AccountActor(toAccount, 1000)), name = toAccount)
    implicit val timeout = Timeout(5 seconds)

    def receive: Receive = {
      case message: TransferMsg =>
        val coordinated = Coordinated()
        coordinated atomic {
          implicit t =>
            from ! coordinated(new AccountDebit(
              message.amtToBeTransferred))
            to ! coordinated(new AccountCredit(
              message.amtToBeTransferred))
        }
      case message: AccountBalance =>
        if (message.accountNumber.equalsIgnoreCase(fromAccount))
          from.tell(message, sender)
        else if (message.accountNumber.equalsIgnoreCase(toAccount))
          to.tell(message, sender)

      case message: AccountDebit =>
        from.tell(message, sender)
      case message: AccountCredit =>
        from.tell(message, sender)
    }

    override val supervisorStrategy = AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
      case _: CoordinatedTransactionException => Resume
      case _: IllegalStateException => Resume
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }
  }
}
