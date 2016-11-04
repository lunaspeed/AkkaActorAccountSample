package net.lunary.account

import java.util.UUID

import akka.actor._
import akka.event.LoggingReceive

object CheckingAccountsService {

  def props = Props(new CheckingAccountsService)
  def name = "accounts"
  private[account] val transactionActorPrefix = "trans"

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

class CheckingAccountsService extends Actor with ActorLogging {

  import CheckingAccountsService._

  def createAccount(name: AccountName, balance: Double) = context.actorOf(Account.props(balance), name)

  //generate controlled name to allow later filter
  def transactionName() = transactionActorPrefix + UUID.randomUUID().toString

  override def receive: Receive = LoggingReceive {
    case aa : AccountAction =>
      context.actorOf(TransactionActor.props(aa, sender(), context), transactionName())
  }

}


