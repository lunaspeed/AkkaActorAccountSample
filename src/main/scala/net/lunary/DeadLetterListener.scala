package net.lunary

import akka.actor.{Actor, ActorSystem, DeadLetter, Props}
import account.CheckingAccounts

/**
  * Created by Lunaspeed on 10/28/16.
  */
class DeadLetterListener extends Actor {

  def receive = {
    case d: DeadLetter => d.sender ! CheckingAccounts.AccountActionError("Account Dead")
  }
}

object DeadLetterListener {

  def listenToDeadLetters(implicit system: ActorSystem) {
    val listener = system.actorOf(Props(classOf[DeadLetterListener]))
    system.eventStream.subscribe(listener, classOf[DeadLetter])
  }
}
