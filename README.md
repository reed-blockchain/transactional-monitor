### Transaction Monitor
---

***
I was only able to work on this project for maybe a week during my time so there's not much here. Hopefully some part of it will be useful somehow in the future.
***

This is a service to continually monitor the crypto transactions flow (just ETH for now) in or out of our payment gateway. Validates if a transaction was successful, generates alert if there is discrepancy between our record and blockchain's or if there is suspicious activities. 

#### Structure

Information for each transaction is stored in a `Transaction` object. All of the transactions that are currently being monitored are stored in the `TransactionTable`. The `TransactionTable` is a set of maps - two for each currency. The first is `Map<UUID, List<PaymentTransactionLeg>>` and maps a `Payment` ID to it's list of transaction legs. The second is `Map<UUID, Transaction>` and maps a `Transaction` ID to the correnponding `Transaction` object.

##### Data Class `Transaction`
`confirmations`
Becomes nonzero when the transaction is included in a block on a chain. Represents the number of blocks on the longest chain since the transaction has been included.
`confirmationBlockNumber`
Number of block including this transaction
`confirmationHeadNumber`
Block number of the head of the longest chain containing this transaction
`drops`
Number of blocks added to the chain *without* the transaction since it was broadcast. Stops incrementing when the transaction is included in a block.

#### Function
This service monitors transactions until they reach 10 confirmations. For each transaction it maintans a pointer to the block it was originally mined in (`Transaction::confirmationBlock`) and head of the longest chain since block that mined it (`Transaction::confirmationHead`). Each new incoming block is compared against these blocks to check for two potentially hazardous cases:
1. The new block's number is less than or equal to the confirmation head's number (minor).
2. The new block's number is less than or equal to the confirmation block's number (major).
