lazy val root = (project in file(".")).
  settings(
    name := "akka-http-microservice-docker-kubernetes",
    organization := "com.montassar",
    version := "0.1",
    scalaVersion := "2.12.12",
    sbtVersion := "1.3.10"
  )


// [Required] Enable plugin and automatically find def main(args:Array[String]) methods from the classpath
enablePlugins(PackPlugin)

lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion    = "2.6.14"

libraryDependencies ++= Seq(
  // AKKA HTTP related dependencies
  "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
  "ch.qos.logback"    % "logback-classic"           % "1.2.3",

  "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
  "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,
  // Used for serialization & Deserialization (Marshalling & Unmarshalling in AKKA terms)
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  // Used to connect to a Cassandra Cluster
  "com.datastax.cassandra" % "cassandra-driver-extras" % "3.8.0",
  // Used for in-memory concurrent cache.
  // The main reason i chose Scaffeine is because it supports caching futures(AsyncLoadingCache)
  "com.github.blemale" %% "scaffeine" % "3.1.0"
)
