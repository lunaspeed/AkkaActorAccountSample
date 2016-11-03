package net.lunary.account

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import net.lunary.{DeadLetterListener, StopSystemAfterAll}
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by Lunaspeed on 10/31/16.
  */
class MassTransactionSpec extends TestKit(ActorSystem("massTransactionTest"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val timeout = Timeout(10.seconds)

  "mass actor test" must {
    "transfer between account" in {
      import CheckingAccounts._


      DeadLetterListener.listenToDeadLetters

      val checkingAccountsActor = system.actorOf(CheckingAccounts.props, "CheckingAccountActor")

      val totalAccountCount = 1000
      val initBalance: Double = 2000
      val accountVector = Vector.newBuilder[String]
      var count = 1
      while (count <= totalAccountCount) {
        val an = s"account$count"
        checkingAccountsActor ! CreateAccount(an, initBalance)
        expectMsg(AccountInfo(List(AccountData(an, initBalance))))
        accountVector += an
        count = count + 1
      }

      val accounts = accountVector.result()
      //Thread.sleep(3000)
//      checkingAccountsActor ! AccountDeposit("account1", 1000)
//
//      expectMsg(AccountInfo(List(AccountData("account1", 3000))))

      val testCount = 100

      for(i <- 0 to testCount) {
        system.actorOf(RandomTransferActor.prop(checkingAccountsActor, accounts, testCount), "randomActor" + (i + 1))
      }

      //wait for transfer to complete, hopefully
      Thread.sleep(10000)

      checkingAccountsActor ! GetAllAccounts

      expectMsgPF() {
        case AccountInfo(accounts) => println("total account balances match initial: "+ (accounts.map(_.balance).sum == (initBalance * totalAccountCount)))
      }
    }
  }
}
