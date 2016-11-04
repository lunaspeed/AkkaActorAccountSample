package net.lunary.account

import akka.actor._
import java.security.SecureRandom

import akka.event.LoggingReceive

object RandomTransferActor {

  def prop(accountsAr: ActorRef, accountList: Seq[String], count: Int) = Props(new RandomTransferActor(accountsAr, accountList, count))
}

/**
  * Transfer between accounts randomly chosen from account list and repeat for certain amount of times.
  */
class RandomTransferActor(checkingAccountsService: ActorRef, accountList: Seq[String], testCount: Int) extends Actor with ActorLogging {

  val rand = new SecureRandom()
  val length = accountList.size

  def randAccount = accountList(rand.nextInt(length))

  for(i <- 1 to testCount) {
    val from = randAccount
    val to = randAccount

    checkingAccountsService ! CheckingAccountsService.Transfer(from, to, rand.nextInt(800))
  }

  override def receive: Receive = LoggingReceive {
    case _ =>
  }
}
