package offset.example.consumer

import java.time.Duration

import consumner.ConsumerFactory
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

class CommitSyncConsumer[K, V](keySerializer: String, valueSerializer: String) extends ConsumerFactory[K, V] {

  override val k = keySerializer
  override val v = valueSerializer

  props.put("enable.auto.commit", "false")
  def consume(topic: List[String]) = {
    def read(consumer: KafkaConsumer[K, V]) = {
      while (true) {
        val a = consumer.poll(Duration.ofMillis(100))
        a.asScala.map(record => record.value()).toList.foreach{record =>
          // Process data here
        }
        consumer.commitSync()
      }
    }
    val consumer = createConsumer
    consumer.subscribe(topic.asJava)
    read(consumer)
  }

}
