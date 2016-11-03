package net.lunary.account

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import net.lunary.{DeadLetterListener, StopSystemAfterAll}
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by Lunaspeed on 10/26/16.
  */
class CheckingAccountsSpec extends TestKit(ActorSystem("testCheckingAccounts"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {

//  "simple test" must {
//    "create account" in {
//      import CheckingAccounts._
//
//
//      DeadLetterListener.listenToDeadLetters
//
//      val checkingAccountsAcexpectMsgtor = system.actorOf(CheckingAccounts.props, "CheckingAccountActor")
//
//      val ac1 = "account1"
//      val ac2 = "account2"
//      val initBalance: Double = 1000
//      checkingAccountsActor ! CreateAccount(ac1, initBalance)
//
//      expectMsg(AccountInfo(List(AccountData(ac1, initBalance))))
//
//      checkingAccountsActor ! CreateAccount(ac2, initBalance)
//
//      expectMsg(AccountInfo(List(AccountData(ac2, initBalance))))
//
//      checkingAccountsActor ! CreateAccount(ac2, initBalance)
//
//      expectMsg(AccountExisted)
//
//      checkingAccountsActor ! Transfer(ac1, ac2, 10)
//
//      expectMsg(AccountInfo(List(AccountData(ac1, 990), AccountData(ac2, 1010))))
//
//
//      checkingAccountsActor ! GetAccount(ac1)
//
//      expectMsg(AccountInfo(List(AccountData(ac1, 990))))
//
//      checkingAccountsActor ! CloseAccount(ac1)
//
//      expectMsg(AccountActionError("cannot close account with balance greater than zero"))
//
//      checkingAccountsActor ! AccountWithdraw(ac1, 990)
//
//      expectMsg(AccountInfo(List(AccountData(ac1, 0))))
//
//      checkingAccountsActor ! CloseAccount(ac1)
//
//      expectMsg(AccountNotExist)
//
//      checkingAccountsActor ! AccountWithdraw(ac1, 990)
//
//      expectMsg(AccountNotExist)
//
//    }
//  }
}
