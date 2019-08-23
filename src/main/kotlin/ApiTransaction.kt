import info.blockchain.common.eth.model.InternalTransaction
import java.math.BigInteger

/**
 * The info.blockchain.common.eth.model Transaction doesn't have the same field names as the api Json data, so
 * in order to use the fromJson function successfully we must define yet another transaction class with the right
 * properties.
 */
data class ApiTransaction(
    val hash: String,
    val blockHash: String,
    val blockNumber: Long,
    val to: String?,
    val from: String,
    val value: BigInteger,
    val nonce: Long,
    val gasPrice: BigInteger,
    val gasLimit: Long,
    val gasUsed: Long?,
    val data: String?,
    val transactionIndex: Long,
    val success: Boolean,
    val state: String,
    val timestamp: Long?,
    val internalTransactions: Collection<InternalTransaction> = emptyList()
)