package net.lunary.account

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import net.lunary.{DeadLetterListener, StopSystemAfterAll}
import org.scalatest.{MustMatchers, WordSpecLike}

class MassTransactionSpec extends TestKit(ActorSystem("massTransactionTest"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {

  "mass simultaneous transactions" must {
    "result in same total balance" in {
      import CheckingAccountsService._

      DeadLetterListener.listenToDeadLetters

      val checkingAccountsService = system.actorOf(CheckingAccountsService.props, "CheckingAccountService")

      val totalAccountCount = 100000
      val initBalance: Double = 2000
      val accountVector = Vector.newBuilder[String]
      var count = 1
      //initialize all accounts
      while (count <= totalAccountCount) {
        val an = s"account$count"
        checkingAccountsService ! CreateAccount(an, initBalance)
        expectMsg(AccountInfo(List(AccountData(an, initBalance))))
        accountVector += an
        count = count + 1
      }

      val accounts = accountVector.result()

      val testCount = 200

      //do mass transfers between random accounts
      for(i <- 0 to testCount) {
        val actor = system.actorOf(RandomTransferActor.prop(checkingAccountsService, accounts, 100), "randomActor" + (i + 1))
        actor ! RandomTransferActor.Start
      }

      //wait for transfer to complete, hopefully
      Thread.sleep(12000)

      checkingAccountsService ! GetAllAccounts

      val balancesSum = expectMsgPF() {
        case AccountInfo(accounts) => accounts.map(_.balance).sum
      }

      assertResult(initBalance * totalAccountCount)(balancesSum)
    }
  }
}
