
import com.blockchain.nabu.kafka.KafkaConsumerSystem
import com.blockchain.nabu.payments.transaction.Payment
import com.blockchain.nabu.topics.Topic
import info.blockchain.json.JsonUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import javax.inject.Inject

class PaymentsKafkaConsumer @Inject constructor(
    consumer: KafkaConsumer<String, String>,
    private val monitor: Monitor
): KafkaConsumerSystem<String, String>(consumer) {

    override val name = "Payments consumer"
    override val noMessageAlertThreshold: Duration = Duration.ofMinutes(1)
    override val topics = listOf(Topic.INTERNAL_EXCHANGE_RATES)//Topic.INCOMING_PAYMENTS_MESSAGE) // Need to add topic and producer to nabu

    // Adds payment info to the TransactionTable
    override fun handleRecord(record: ConsumerRecord<String, String>) {
        val payment = JsonUtil.fromJson<Payment<*>>(record.value())
        monitor.receivePayment(payment)
    }

}