
import com.blockchain.nabu.model.Currency
import java.time.Instant

data class Transaction(
        val hash: String,
        val currency: Currency,
        val from: String,
        val to: String,
        var confirmations: Int = 0,
        var confirmationBlockNumber: Long? = null,
        var confirmationHeadNumber: Long? = null,
        var drops: Int = 0,
        val insertedAt: Instant = Instant.now(),
        var updatedAt: Instant? = null
)