package offset_example.consumer

import java.time.Duration

import consumner.ConsumerFactory
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

class AutoCommitConsumer[K, V](keySerializer: String, valueSerializer: String) extends ConsumerFactory[K, V] {

  override val k = keySerializer
  override val v = valueSerializer

  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "100")
  def consume(topic: List[String]) = {
    def read(consumer: KafkaConsumer[K, V]) = {
      while (true) {
        val a = consumer.poll(Duration.ofMillis(100))
        a.asScala.map(record => record.value()).toList.foreach{record =>
          // Process data here
        }
      }
    }
    val consumer = createConsumer
    consumer.subscribe(topic.asJava)
    read(consumer)
  }

}