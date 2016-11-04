package net.lunary.account

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.lunary.StopSystemAfterAll
import org.scalatest.{MustMatchers, WordSpecLike}
/**
  * Created by Lunaspeed on 10/25/16.
  */
class AccountSpec extends TestKit(ActorSystem("testAccounts"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "accounts" must {
    "be able to deposit and withdraw" in {
      import Account._

      val initBalance: Double = 2000
      val accountName = "testAccount1"
      val accountActor = system.actorOf(Account.props(initBalance), accountName)

      accountActor ! Withdraw(100)

      expectMsg(Balance(1900))

      accountActor ! Withdraw(900)

      expectMsg(Balance(1000))

      accountActor ! Deposit(10)

      expectMsg(Balance(1010))
    }
  }
}
