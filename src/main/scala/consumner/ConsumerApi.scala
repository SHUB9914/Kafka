package consumner
import java.time.Duration

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

class ConsumerApi[K,V](keySerializer: String, valueSerializer: String) extends ConsumerFactory[K,V] {

  override val k = keySerializer
  override val v = valueSerializer

  def consume(topic : List[String]) = {

    def read(consumer : KafkaConsumer[K,V]) = {
      while (true) {
        val a = consumer.poll(Duration.ofMillis(100))
          a.asScala.map(record => record.value()).toList.foreach(println)
      }
    }

    val consumer = createConsumer
    consumer.subscribe(topic.asJava)
    read(consumer)
    consumer.commitAsync()
  }

}

object TestConsumer extends App {


  val keyDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer"
  val valueDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer"

  val con = new ConsumerApi[String , String](keyDeserializer , valueDeserializer)

  con.consume(List("test"))
}
