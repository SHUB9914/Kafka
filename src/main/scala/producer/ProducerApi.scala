package producer

import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}

import scala.concurrent.Promise


class ProducerApi[K, V](keySerializer: String, valueSerializer: String) extends ProducerFactory[K, V] {

  override val k: String = keySerializer
  override val v: String = valueSerializer
  val kafkaProducer = createKafkaProducer

  def send(record: ProducerRecord[K, V]) = {
    val promise = Promise[RecordMetadata]()
    val callBack = new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        Option(exception) match {
          case None => promise.success(metadata)
          case Some(ex) =>promise.failure(ex)
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

  val api = new ProducerApi[String, String](keySerializer, valueSerializer)

  for(i <- 1 to 600){
    println("Sending>>>>> = "+ i)
    val producerRecord = new ProducerRecord[String, String]("test", "xyz", "This is second message = " + i)

    api.send(producerRecord)
  }

  api.close



}
