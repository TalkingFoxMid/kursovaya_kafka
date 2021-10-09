package app

import app.KafkaClusterManagerDomain.FailedToCreateTopic
import cats.effect.IO
import cats.syntax.all._
import cats.effect.kernel.Sync

import java.lang.Throwable
import sys.process._
trait KafkaClusterManager[F[_]]

class KafkaClusterManagerImpl[F[_]: Sync](zkHosts: List[String]) extends KafkaClusterManager[F] {
  def createTopic(name: String): F[Unit] =
    runWithBrokers[Unit](
      zk => Sync[F].delay {
        s"""kafka-2.8.0-src/bin/kafka-topics.sh
           |--create
           |--zookeeper ${zk}
           |--replication-factor 1
           |--partitions 1
           |--topic ${name}""".!!},
      FailedToCreateTopic(name)
    )

  private def runWithBrokers[A](action: String => F[A], t: Throwable):F[A] =
    zkHosts.foldLeft(Sync[F].raiseError[A](t))(
      (res, nextBroker)=> res.handleErrorWith(_ => action(nextBroker))
    )
}
