package object app {
  sealed trait KafkaResult
  case class KafkaSuccess(a: String) extends KafkaResult
  case class KafkaFailed(a: String) extends KafkaResult
  object KafkaClusterManagerDomain {
    sealed trait TopicCreationResult
    case class FailedToCreateTopic(topicName: String) extends Exception
    case class TopicCreated(topicName: String) extends TopicCreationResult
  }
}
