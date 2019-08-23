
import com.blockchain.nabu.json.fromJson
import com.blockchain.nabu.model.Currency
import com.blockchain.nabu.payments.transaction.Payment
import com.blockchain.nabu.payments.transaction.PaymentTransactionLeg
import com.blockchain.nabu.payments.transaction.TransactionLegType
// Not sure what nexus package contains this file
// import com.blockchain.nabu.payments.db.repository.TransactionRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Singleton

@Singleton
class TransactionTable() {
    private var ethPaymentMap = hashMapOf<UUID, Map<TransactionLegType, PaymentTransactionLeg>>()
    private var ethTransactionMap = hashMapOf<UUID, Transaction>()
    val transactions: Collection<Transaction> get() = ethTransactionMap.values

    fun addPayment(payment: Payment<*>) {
        val currency = payment.blockchainAccount.currency
        val paymentMap = getPaymentMap(currency)
        val transactionMap = getTransactionMap(currency)
        paymentMap[payment.id] = payment.legs
        payment.legs.forEach { transactionMap[it.value.transaction] = buildTransactionFromId(it.value.transaction, currency) }
    }

    fun removePayment(payment: Payment<*>): Boolean {
        val currency = payment.blockchainAccount.currency
        val paymentMap = getPaymentMap(currency)
        val transactionMap = getTransactionMap(currency)
        if (paymentMap.any { entry -> entry.key == payment.id}) {
            payment.legs.forEach { transactionMap.remove(it.value.transaction) }
            paymentMap.remove(payment.id)
            return true
        }
        return false
    }

    private fun buildTransactionFromId(transactionId: UUID, currency: Currency): Transaction {
        val hash = "0x0" // TODO: get transaction hash from ID from the TransactionRepository
        val baseUrl = "https://api.blockchain.info/v2/eth/data/transaction/"
        val fullUrl = "$baseUrl?hash=$hash"

        /**
         * For some reason using the ApacheHttpClient.sendGetRequest returns a 500 status error. I think it has
         * something to do with the headers it uses but the API doesn't allow changing/setting them so it can't
         * be used here. Sigh.
         */
        val conn: HttpURLConnection = URL(fullUrl).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val response = StringBuffer()
        var line = reader.readLine()
        while (line != null) {
            response.append(line)
            line = reader.readLine()
        }
        val tx: ApiTransaction = fromJson(response.toString())
        val blockNumber: Long = tx.blockNumber
        return Transaction(
            hash = tx.hash,
            currency = currency,
            from = tx.from,
            to = tx.to ?: error(""),
            confirmations = (getLatestBlockNumber() - blockNumber) as Int,
            confirmationBlockNumber = blockNumber,
            confirmationHeadNumber = getLatestBlockNumber() // TODO: make sure there wasn't a fork anywhere
        )
    }

    private fun getLatestBlockNumber(): Long {
        val url = "https://api.blockchain.info/v2/eth/data/block/latest/number"
        val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        return reader.readLine().toLong()
    }

    /**
     * Returns the corresponding maps given a currency. This was originally designed to support more currencies than
     * just ETH.
     */
    private fun getPaymentMap(currency: Currency): HashMap<UUID, Map<TransactionLegType, PaymentTransactionLeg>> {
        return when (currency) {
            Currency.ETH -> ethPaymentMap
            else -> throw IllegalArgumentException("unrecgonised currency symbol: $currency")
        }
    }

    private fun getTransactionMap(currency: Currency): HashMap<UUID, Transaction> {
        return when (currency) {
            Currency.ETH -> ethTransactionMap
            else -> throw IllegalArgumentException("unrecgonised currency symbol: $currency")
        }
    }
}