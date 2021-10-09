package app

import cats.effect.kernel.Sync
import cats.{Applicative, ApplicativeError, ApplicativeThrow}
import cats.syntax.all._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties

class KafkaProducerF[F[_]: Sync](port: Int) {
  private val properties: Properties =
    {
      val props = new Properties()
      props.put("bootstrap.servers", s"localhost:$port")
      props.put("acks", "all")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000)
      props.put(ProducerConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG, 1000)
      props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000)
      props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 2000)
      props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
      props
    }
  private val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](properties)
  def send(topic: String, msg: String): F[KafkaResult] =
    Sync[F].delay(producer.send(new ProducerRecord[String, String](topic, msg)).get())
      .as(KafkaSuccess(msg): KafkaResult).handleError(_ => KafkaFailed(msg))
}
