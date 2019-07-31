package consumner

import java.time.Duration

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class CombiningSyncAndAsync[K, V](keySerializer: String, valueSerializer: String) extends ConsumerFactory[K, V] {

  val log = LoggerFactory.getLogger(this.getClass)

  override val k = keySerializer
  override val v = valueSerializer

  props.put("enable.auto.commit", "false")

  def consume(topic: List[String]) = {
    def read(consumer: KafkaConsumer[K, V]) = {
      try {
        while (true) {
          val a = consumer.poll(Duration.ofMillis(100))
          a.asScala.map(record => record.value()).toList.foreach { record =>
            // Process data here
          }
          consumer.commitAsync()
        }
      } catch {
        case e: Exception => log.error("Unexpected error", e)
          consumer.commitSync()
          consumer.close()
      }
    }
    val consumer = createConsumer
    consumer.subscribe(topic.asJava)
    read(consumer)
  }

}