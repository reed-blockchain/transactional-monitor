import com.blockchain.nabu.payments.transaction.Payment
import info.blockchain.ethereum.DataModel

class Monitor(private val table: TransactionTable) {

    fun <T> update(obj: T) {
        when (obj) {
            is DataModel.Block -> update(obj)
            is DataModel.Transaction -> update(obj)
            is DataModel.Account -> update(obj)
            is DataModel.AccountDelta -> update(obj)
            is DataModel.TokenAccount -> update(obj)
            is DataModel.TokenAccountDelta -> update(obj)
            is DataModel.TokenTransfer -> update(obj)
        }
    }

    fun receivePayment(payment: Payment<*>) = table.addPayment(payment)

    private fun update(block: DataModel.Block) {
        val blockNumber = block.header.number
        table.transactions.forEach { check(it, blockNumber) }
    }

    private fun update(transaction: DataModel.Transaction) {

    }

    private fun update(account: DataModel.Account) {

    }

    private fun update(accountDelta: DataModel.AccountDelta) {

    }

    private fun update(tokenAccount: DataModel.TokenAccount) {

    }

    private fun update(tokenAccountDelta: DataModel.TokenAccountDelta) {

    }

    private fun update(tokenTransfer: DataModel.TokenTransfer) {

    }

    private fun check(transaction: Transaction, blockNumber: Long) {
        // Make sure that the transaction here is a reference to the transaction table one so changes stick

    }
}