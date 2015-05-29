package com.zqh.akka.effective

import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive

class CheckingAccountsProxyStub extends CheckingAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> List((3L, BigDecimal(15000))),
    2L -> List((6L, BigDecimal(640000)), (7L, BigDecimal(1125000)), (8L, BigDecimal(40000))))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! CheckingAccountBalances(Some(data))
        case None => sender ! CheckingAccountBalances(Some(List()))
      }
  }
}

class SavingsAccountsProxyStub extends SavingsAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    1L -> (List((1L, BigDecimal(150000)), (2L, BigDecimal(29000)))),
    2L -> (List((5L, BigDecimal(80000)))))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! SavingsAccountBalances(Some(data))
        case None => sender ! SavingsAccountBalances(Some(List()))
      }
  }
}

class MoneyMarketAccountsProxyStub extends MoneyMarketAccountsProxy with ActorLogging {
  val accountData = Map[Long, List[(Long, BigDecimal)]](
    2L -> List((9L, BigDecimal(640000)), (10L, BigDecimal(1125000)), (11L, BigDecimal(40000))))

  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Received GetCustomerAccountBalances for ID: $id")
      accountData.get(id) match {
        case Some(data) => sender ! MoneyMarketAccountBalances(Some(data))
        case None => sender ! MoneyMarketAccountBalances(Some(List()))
      }
  }
}

class TimingOutSavingsAccountProxyStub extends SavingsAccountsProxy with ActorLogging {
  def receive = LoggingReceive {
    case GetCustomerAccountBalances(id: Long) =>
      log.debug(s"Forcing timeout by not responding!")
  }
}
