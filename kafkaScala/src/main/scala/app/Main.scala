package app

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.syntax.all._

import java.time.{LocalDateTime, LocalTime, OffsetDateTime}
import scala.concurrent.duration._
import sys.process._
object Main extends IOApp {
  final val topic = "test6"
  override def run(args: List[String]): IO[ExitCode] =
    for {
      prod <- Sync[IO].delay(
        new KafkaProducerF[IO](9095)
      )
      consumer <- Sync[IO].delay(
        new KafkaConsumerF[IO](9095)
      )

     clusterManager <- Sync[IO].delay(
        new KafkaClusterManagerImpl[IO]("localhost:2181" :: "localhost:2182" :: "localhost: 2183" :: Nil)
      ).flatMap(_.createTopic(topic))
      .handleErrorWith(_ => IO.unit)

      f <- List.range(1, 10000).map(_.toString).traverse { msg =>
        for {
          time <- IO(System.currentTimeMillis())
          res <- prod.send(topic, msg)

          c <- consumer.readAll(topic).map((_, res)).flatMap {
            case (_, _: KafkaFailed) => IO(println("failed to write"))
            case (List(value), _) if value == msg => IO(println(s"writen $msg and read $msg"))
            case (l, _: KafkaSuccess) => IO(println(s"!!!! WRITEN $msg BUT FOUND $l !!!!"))
          }
          timeAfterSend <- IO(System.currentTimeMillis())
          _ <- IO(println(s"send by ${(timeAfterSend - time).milli} micros"))
        } yield ()
      }

    } yield ExitCode.Success
}
