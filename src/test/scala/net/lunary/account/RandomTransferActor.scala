package net.lunary.account

import akka.actor._
import java.security.SecureRandom

import akka.event.LoggingReceive

object RandomTransferActor {

  def prop(accountsAr: ActorRef, accountList: Seq[String], count: Int) = Props(new RandomTransferActor(accountsAr, accountList, count))
}

/**
  * Created by Lunaspeed on 10/31/16.
  */
class RandomTransferActor(accountsAr: ActorRef, accountList: Seq[String], count: Int) extends Actor with ActorLogging {

  Thread.sleep(1000)

  val rand = new SecureRandom()
  val length = accountList.size

  def randAccount = accountList(rand.nextInt(length))

  for(i <- 1 to count) {
    val from = randAccount
    val to = randAccount

    accountsAr ! CheckingAccounts.Transfer(from, to, rand.nextInt(800))
  }

  override def receive: Receive = LoggingReceive {
    case _ =>
  }
}
