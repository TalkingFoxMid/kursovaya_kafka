name := "kafkaScala"

version := "0.1"

scalaVersion := "2.13.6"

lazy val core = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.2.7",
      "org.apache.kafka" %% "kafka" % "2.8.0"
    )
  )