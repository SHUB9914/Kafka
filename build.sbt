
name := "Kafka_POC"

version := "0.1"

scalaVersion := "2.12.8"

val slf4j = "org.slf4j" % "slf4j-api" % "1.7.25"
val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.1.1",
  "org.typelevel" %%   "cats-core" % "1.5.0",
  slf4j,
  logback
)