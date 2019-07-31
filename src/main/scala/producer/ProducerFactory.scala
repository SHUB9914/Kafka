package producer

import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer

trait ProducerFactory[K, V] {
  val k : String
  val v : String

 lazy val props: Properties = {
   val props = new Properties()
   props.put("bootstrap.servers", "localhost:9092")
   props.put("acks","all")
   props.put("retries","3")
   props.put("batch.size","10")
   props.put("linger.ms","5")
   props.put("key.serializer", k)
   props.put("value.serializer", v)

   props
 }

  def createKafkaProducer = new KafkaProducer[K, V](props)

}
