package app

import cats.effect.kernel.Sync
import cats.syntax.all._
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters._
class KafkaConsumerF[F[_]: Sync](port: Int) {
  private val properties: Properties =
    {
      val props = new Properties()
      import org.apache.kafka.clients.consumer.ConsumerConfig
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, s"localhost:$port")
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer")
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
      props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
      props
    }
  private val c: KafkaConsumer[String, String] =
    new KafkaConsumer[String, String](properties)

  def readAll(topic: String): F[List[String]] =
    for {

      _ <- Sync[F].delay(c.subscribe(List(topic).asJava))

      res <- Sync[F].delay(
        c.poll(Duration.ofSeconds(4)).iterator().asScala.map(_.value()).toList
      )
    } yield res

}
