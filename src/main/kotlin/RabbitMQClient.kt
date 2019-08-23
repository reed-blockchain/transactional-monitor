

import info.blockchain.ethereum.DataModel
import info.blockchain.pubsub.Subscriber
import info.blockchain.pubsub.Message
import info.blockchain.pubsub.rabbitmq.RabbitPubsubClient
import info.blockchain.ethereum.topic.Topic
import javax.inject.Inject
import javax.inject.Named

class RabbitMQClient @Inject constructor(
    @Named("rabbitmqPort") private val port: Int,
    @Named("rabbitmqHost") private val host: String,
    @Named("rabbitmqPassword") private val password: String,
    @Named("rabbitmqUsername") private val username: String,
    @Named("rabbitmqVHost") private val vhost: String,
    @Named("rabbitmqNodeName") private val nodeName: String
) {
    private val client = RabbitPubsubClient.builder()
        .withPort(port)
        .withHostname(host)
        .withPassword(password)
        .withUsername(username)
        .withNamespace(vhost)
        .withNodeName(nodeName)
        .build()

    fun listen(topic: Topic, monitor: Monitor) {
        client.subscribeToBytes(topic.topicName, getSubscriberFromTopic(topic, monitor))
    }

    private fun getSubscriberFromTopic(topic: Topic, monitor: Monitor): RabbitSubscriber<*> {
        return when (topic) {
            Topic.BLOCK -> RabbitSubscriber<DataModel.Block>(monitor, DataModel.Block::parseFrom)
            Topic.CTX, Topic.PTX -> RabbitSubscriber<DataModel.Transaction>(monitor, DataModel.Transaction::parseFrom)
            Topic.ACCOUNT -> RabbitSubscriber<DataModel.Account>(monitor, DataModel.Account::parseFrom)
            Topic.ACCOUNT_DELTA -> RabbitSubscriber<DataModel.AccountDelta>(monitor, DataModel.AccountDelta::parseFrom)
            Topic.TOKEN_ACCOUNT -> RabbitSubscriber<DataModel.TokenAccount>(monitor, DataModel.TokenAccount::parseFrom)
            Topic.TOKEN_ACCOUNT_DELTA -> RabbitSubscriber<DataModel.TokenAccountDelta>(monitor, DataModel.TokenAccountDelta::parseFrom)
            Topic.TOKEN_TRANSFER -> RabbitSubscriber<DataModel.TokenTransfer>(monitor, DataModel.TokenTransfer::parseFrom)
        }
    }
}

class RabbitSubscriber<T>(private val monitor: Monitor, val parse: (ByteArray) -> T) : Subscriber<ByteArray> {
    override fun consume(msg: Message<ByteArray>) {
        val obj: T = parse(msg.payload)
        monitor.update(obj)
    }
}