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

  "checkingAccountsService" must {
    "be able to manipulate 2 different accounts" in {
      import CheckingAccountsService._

      DeadLetterListener.listenToDeadLetters

      val checkingAccountsService = system.actorOf(CheckingAccountsService.props, "CheckingAccountActor")

      val ac1 = "account1"
      val ac2 = "account2"
      val initBalance: Double = 1000

      checkingAccountsService ! CreateAccount(ac1, initBalance)

      expectMsg(AccountInfo(List(AccountData(ac1, initBalance))))

      checkingAccountsService ! CreateAccount(ac2, initBalance)

      expectMsg(AccountInfo(List(AccountData(ac2, initBalance))))

      checkingAccountsService ! CreateAccount(ac2, initBalance)

      expectMsg(AccountExisted)

      checkingAccountsService ! Transfer(ac1, ac2, 10)

      expectMsg(AccountInfo(List(AccountData(ac1, 990), AccountData(ac2, 1010))))


      checkingAccountsService ! GetAccount(ac1)

      expectMsg(AccountInfo(List(AccountData(ac1, 990))))

      checkingAccountsService ! CloseAccount(ac1)

      expectMsg(AccountActionError("cannot close account with balance greater than zero"))

      checkingAccountsService ! AccountWithdraw(ac1, 990)

      expectMsg(AccountInfo(List(AccountData(ac1, 0))))

      checkingAccountsService ! CloseAccount(ac1)

      expectMsg(AccountNotExist)

      checkingAccountsService ! AccountWithdraw(ac1, 990)

      expectMsg(AccountNotExist)

    }
  }
}
