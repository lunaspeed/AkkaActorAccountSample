package net.lunary.account

import akka.actor._
import akka.event.LoggingReceive

/**
  * Created by Lunaspeed on 10/25/16.
  */
object CheckingAccounts {

  def props = Props(new CheckingAccounts)
  def name = "accounts"

  type AccountName = String

  sealed trait AccountAction {
    val name: AccountName
  }
  case class CreateAccount(name: AccountName, balance: Double) extends AccountAction
  case class CloseAccount(name: AccountName) extends AccountAction
  case class GetAccount(name: AccountName) extends AccountAction
  case class AccountDeposit(name: AccountName, amount: Double) extends AccountAction
  case class AccountWithdraw(name: AccountName, amount: Double) extends AccountAction
  case class Transfer(from: AccountName, to: AccountName, amount: Double) extends AccountAction {
    val name = from
  }

  case object GetAllAccounts extends AccountAction {
    val name = "GetAllAccounts"
  }


  sealed trait AccountResponse
  case class AccountInfo(account: List[AccountData]) extends AccountResponse
  case object AccountExisted extends AccountResponse
  case object AccountNotExist extends AccountResponse
  case class AccountActionError(message: String) extends AccountResponse

  case class AccountData(name: AccountName, balance: Double)

}

class CheckingAccounts extends Actor with ActorLogging {


  import CheckingAccounts._


  def createAccount(name: AccountName, balance: Double) = context.actorOf(Account.props(balance), name)

  override def receive: Receive = LoggingReceive {

    case aa : AccountAction =>
      context.actorOf(TransactionActor.props(aa, sender(), context))


  }

//  implicit class ActorPimp(ar: ActorRef) {
//    implicit def askForAccount(action: Account.BalanceAction): Future[BalanceResponse] = {
//      ar.ask(action).mapTo[BalanceResponse]
//    }
//  }
//
//  def createAccount(name: AccountName, balance: Double) =
//    context.actorOf(Account.props(name, balance), name)
//
//  def account(name: AccountName) = context.child(name)
//
//  def toAccountResponse(name:AccountName)(br: BalanceResponse) = br match {
//    case Closed => AccountNotExist
//    case BalanceError(msg) => AccountActionError(msg)
//    case Balance(b) => AccountInfo(List(AccountData(name, b)))
//    case InsufficientBalance => AccountActionError("account $name does not have sufficient balance")
//    case _ => AccountActionError(s"unexpected response $br")
//  }
//
//  def replyNotExist = sender() ! AccountNotExist
//
//  def askAccount(child: ActorRef, action: Account.BalanceAction): Future[BalanceResponse] = {
//    child.ask(action).mapTo[BalanceResponse]
//  }
//
//  def pipeToSender(name: AccountName, action: Account.BalanceAction) (child: ActorRef) = {
//    pipe(askAccount(child, action) map toAccountResponse(name)) to sender()
//  }
//
//  def forwardToAccount(name: String, action: Account.BalanceAction) =
//    account(name).fold(replyNotExist)(pipeToSender(name, action))
//
//  def receive = {
//    case CreateAccount(_, balance) if balance < 0 => sender() ! AccountActionError("balance cannot be negative")
//    case CreateAccount(name, balance) =>
//      def create() = {
//        createAccount(name, balance)
//        sender() ! AccountInfo(List(AccountData(name, balance)))
//      }
//      account(name).fold(create()) {_ => sender() ! AccountExisted}
//
//    case CloseAccount(name) => forwardToAccount(name, Account.Close)
//
//    case GetAccount(name) => forwardToAccount(name, Account.GetBalance)
//
//    case AccountDeposit(name, amount) => forwardToAccount(name, Account.Deposit(amount))
//
//    case AccountWithdraw(name, amount) => forwardToAccount(name, Account.Withdraw(amount))
//
//    case Transfer(from, to, amount) =>
//
//      val result = (account(from), account(to)) match {
//        case (Some(fromAr), Some(toAr)) => askAccount(fromAr, Withdraw(amount)).flatMap({
//          case BalanceError(error) => Future(AccountActionError(error))
//          case InsufficientBalance => Future(AccountActionError("insufficient balance"))
//          case Balance(fromBalance) => askAccount(toAr, Deposit(amount)).map({
//
//            case Balance(toBalance) => AccountInfo(List(AccountData(from, fromBalance), AccountData(to, toBalance)))
//            case r => AccountActionError(s"unexpected result $r from withdraw")
//          })
//          case r => Future(AccountActionError(s"unexpected result $r from deposit")) //@TODO rollback withdraw?
//        })
//
//        case _ => Future(AccountNotExist)
//      }
//
//      pipe(result) to sender()
//  }
}


