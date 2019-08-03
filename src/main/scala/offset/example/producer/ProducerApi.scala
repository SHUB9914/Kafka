package offset.example.producer

import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}
import org.slf4j.LoggerFactory
import producer.ProducerFactory

import scala.concurrent.Promise


class ProducerApi[K, V](keySerializer: String, valueSerializer: String) extends ProducerFactory[K, V] {

  val logger = LoggerFactory.getLogger(this.getClass)

  override val k: String = keySerializer
  override val v: String = valueSerializer
  val kafkaProducer = createKafkaProducer

  def send(record: ProducerRecord[K, V]) = {
    val promise = Promise[RecordMetadata]()
    val callBack = new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        Option(exception) match {
          case None => promise.success(metadata)
          case Some(ex) =>logger.error("Some thing went wrong in message sending " + ex.printStackTrace())
            promise.failure(ex)
        }
      }
    }
    kafkaProducer.send(record, callBack)
    promise.future
  }
  def close = kafkaProducer.close()

}

object TestProducer extends App {
  val keySerializer: String = "org.apache.kafka.common.serialization.StringSerializer"
  val valueSerializer: String = "org.apache.kafka.common.serialization.StringSerializer"
  val producer = new ProducerApi[String, String](keySerializer, valueSerializer)
    for(i <- 1 to 100){
      val record1 = new ProducerRecord[String, String]("test", 0,"xyz", "This is partition1 = " + i)
      val record2 = new ProducerRecord[String, String]("test", 1,"xyz", "This is partition2 = " + i)
      producer.send(record1)
      producer.send(record2)
    }
  producer.close

}
