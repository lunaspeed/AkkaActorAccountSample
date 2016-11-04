package net.lunary.account

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.{ask, pipe}

import CheckingAccountsService._
import Account._


/**
  * Created by Lunaspeed on 10/28/16.
  */
private[account] object TransactionActor {

  def props(request: AccountAction, originalSender: ActorRef, callerContext: ActorContext) =
    Props(new TransactionActor(request, originalSender, callerContext))

}

private[account] class TransactionActor(request: AccountAction, originalSender: ActorRef, callerContext: ActorContext) extends Actor with ActorLogging {

  import context._

  request match {
    case CreateAccount(_, balance) if balance < 0 => killAfter {
      originalSender ! AccountActionError("balance cannot be negative")
    }
    case CreateAccount(name, balance) =>
      def create() = {
        createAccount(balance)
        originalSender ! AccountInfo(List(AccountData(name, balance)))
      }
      killAfter {
        account(name).fold(create()) { _ => originalSender ! AccountExisted }
      }

    case CloseAccount(name) => sendBalanceAction(name, Account.Close)

    case GetAccount(name) => sendBalanceAction(name, Account.GetBalance)

    case AccountDeposit(name, amount) => sendBalanceAction(name, Account.Deposit(amount))

    case AccountWithdraw(name, amount) => sendBalanceAction(name, Account.Withdraw(amount))

    case t @ Transfer(from, to, amount) =>
      sendBalanceAction(from, Account.Withdraw(amount))
      context.become(waitWithdrawReply(t))

    case GetAllAccounts =>

      implicit val timeout = Timeout(3 seconds)

      def getBalances = callerContext.children
        .filter(!_.path.name.startsWith(transactionActorPrefix))
        .map { child =>
          child.ask(Account.GetBalance)
            .mapTo[Account.Balance]
            .map((child.path.name, _))
        }

      def toAccountInfo(f: Future[Iterable[(String, Account.Balance)]]) = {

        f.map(_.map(ab => AccountData(ab._1, ab._2.balance)))
          .map(l => AccountInfo(l.toList))
      }
      killAfter {
        pipe(toAccountInfo(Future.sequence(getBalances))) to originalSender
      }
  }

  override def receive: Receive = LoggingReceive {
      case br: BalanceResponse =>  killAfter {
        originalSender ! toAccountResponse(br)
      }
  }

  def waitWithdrawReply(trans: Transfer): Receive = LoggingReceive {
    case Balance(b) =>
      sendBalanceAction(trans.to, Account.Deposit(trans.amount))
      context.become(waitDepositReply(AccountData(trans.from, b), trans))

    case InsufficientBalance => killAfter {
      originalSender ! toAccountResponse(InsufficientBalance)
    }
  }

  def waitDepositReply(withdrawData: AccountData, trans: Transfer): Receive = LoggingReceive {
      case Balance(b) => killAfter {originalSender ! AccountInfo(List(withdrawData, AccountData(trans.to, b))) }
      case br: BalanceResponse => killAfter { originalSender ! toAccountResponse(br) } //TODO if is error need to rollback withdraw
  }

  def killAfter[A](f: => A): A = {
    val af = f
    self ! PoisonPill
    af
  }

  def createAccount(balance: Double) = callerContext.actorOf(Account.props(balance), request.name)

  def account(name: AccountName) = callerContext.child(name)

  def replyNotExist = originalSender ! AccountNotExist

  def sendBalanceAction(name: AccountName, action: BalanceAction) =
    account(name).fold(replyNotExist) {_ ! action}

  def toAccountResponse(br: BalanceResponse) = br match {
    case Closed => AccountNotExist
    case BalanceError(msg) => AccountActionError(msg)
    case Balance(b) => AccountInfo(List(AccountData(request.name, b)))
    case InsufficientBalance => AccountActionError("account $name does not have sufficient balance")
    case _ => AccountActionError(s"unexpected response $br")
  }

}
