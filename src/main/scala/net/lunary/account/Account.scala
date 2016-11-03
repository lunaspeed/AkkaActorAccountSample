package net.lunary.account

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

/**
  * Created by Lunaspeed on 10/25/16.
  */
object Account {

  def props(balance: Double = 0) = Props(new Account(balance))

  sealed trait BalanceAction
  case class Deposit(amount: Double) extends BalanceAction
  case class Withdraw(amount: Double) extends BalanceAction
  case object GetBalance extends BalanceAction
  case object Close extends BalanceAction

  sealed trait BalanceResponse
  case object InsufficientBalance extends BalanceResponse
  case class Balance(balance: Double) extends BalanceResponse
  case class BalanceError(message: String) extends BalanceResponse
  case object Closed extends BalanceResponse
}

class Account(var balance: Double = 0) extends Actor with ActorLogging {

  import Account._

  def receive = LoggingReceive {
    case Deposit(a) =>
      balance += a
      sender() ! Balance(balance)

    case Withdraw(a) if balance < a => sender() ! InsufficientBalance

    case Withdraw(a) =>

      balance -= a
      sender() ! Balance(balance)

    case GetBalance => sender() ! Balance(balance)

    case Close if balance > 0 => sender() ! BalanceError("cannot close account with balance greater than zero")

    case Close =>
      sender() ! Closed
      context.stop(self)
  }


}
