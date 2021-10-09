package app.USE

import app.KafkaConsumerF
import app.Main.topic
import cats.effect.{ExitCode, IO, IOApp, Sync}

object ReadAll extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      consumer <- Sync[IO].delay(
        new KafkaConsumerF[IO](9094)
      )
      _ <- consumer.readAll(topic).map(println(_))
      _ <- consumer.readAll(topic).map(println(_))
      _ <- consumer.readAll(topic).map(println(_))
    } yield ExitCode.Success
}
