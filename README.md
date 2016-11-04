### Introduction

This is a sample to demonstrate the use Akka actor on concurrent account transactions,
to show how actor easily keep the integrity of all accounts with out using synchronisation or lock explicitly.

#### Main Layout

##### CheckingAccountsService

The only open actor to be used outside.
It is responsible to handling incoming request against Account.
It has its own set of messages defined for receiving and replying, to hide any implementation under the hood.

##### Account
 
The simple model that represents the real-world bank account.
It accepts the basic action messages such as Deposit, Withdraw and Close,
and maintains its own balance information.

##### TransactionActor

A "per request" actor which takes the "actual work load" off CheckingAccountsService to allow it to handle more request.
It is responsible for transforming and send the message from CheckingAccountsService to Account and transforming the message
from Account and reply to the original sender to complete the whole request.



### Testing

##### RandomTransferActor

Used to send out continuous request to make Transfer between 2 random accounts. 
