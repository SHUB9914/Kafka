package offset.example.consumer

import java.time.Duration

import consumner.ConsumerFactory
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


class RebalanceConsumer[K, V](keySerializer: String, valueSerializer: String) extends ConsumerFactory[K, V] {

  val log = LoggerFactory.getLogger(this.getClass)

  override val k = keySerializer
  override val v = valueSerializer

  props.put("enable.auto.commit", "false")

  def consume(topic: List[String]) = {
    val consumer: KafkaConsumer[K, V] = createConsumer
    val rebalanceListener = new RebalanceListener[K, V](consumer)
    consumer.subscribe(topic.asJava, rebalanceListener)
    read(consumer)

    def read(consumer: KafkaConsumer[K, V]) = {
      try {
        while (true) {
          val a = consumer.poll(Duration.ofMillis(100))
          a.asScala.toList.foreach { record =>
            // Process data here
            rebalanceListener.addOffset(record.topic(),record.partition(),record.offset())
          }
          consumer.commitAsync()
        }
      } catch {
        case e: Exception => log.error("Unexpected error", e)
      }
      finally {
        consumer.close()
      }
    }
  }
}

object RebalaceConsumerTest extends App {

  val keyDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer"
  val valueDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer"

  val con = new RebalanceConsumer[String , String](keyDeserializer , valueDeserializer)

  con.consume(List("test"))
}