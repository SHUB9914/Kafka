package consumner

import java.util

import org.apache.kafka.clients.consumer.{ConsumerRebalanceListener, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class RebalanceListener[K,V](consumer : KafkaConsumer[K,V]) extends ConsumerRebalanceListener {

  var currentOffset : Map[TopicPartition,OffsetAndMetadata] = Map()

  val log = LoggerFactory.getLogger(this.getClass)

  def addOffset(topic : String , partition : Int , offset : Long) = {
     val newOffset = Map(new TopicPartition(topic,partition) -> new OffsetAndMetadata(offset , "commit"))
    currentOffset ++ newOffset
  }

  def commit = {
    consumer.commitAsync(currentOffset.asJava , null)
    currentOffset = currentOffset.empty
  }

  override def onPartitionsRevoked(partitions: util.Collection[TopicPartition]): Unit = {
    log.info("Following partition revoked")
    partitions.asScala.foreach(p => log.info(p + ","))
    log.info("Committing the offset")
    consumer.commitSync()
    currentOffset = currentOffset.empty
  }

  override def onPartitionsAssigned(partitions: util.Collection[TopicPartition]): Unit = {
    partitions.asScala.foreach(p => log.info("Assigned paration is " + p.partition))

  }
}
