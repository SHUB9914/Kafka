package consumner

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer

trait ConsumerFactory[K,V] {
  val k : String
  val v : String

  lazy val props: Properties = {
    val prop = new Properties()
    prop.put("bootstrap.servers", "localhost:9092")
    prop.put("group.id", "xyz")
    prop.put("key.deserializer", k)
    prop.put("value.deserializer", v)
    prop
  }

  def createConsumer = new KafkaConsumer[K,V](props)

}
