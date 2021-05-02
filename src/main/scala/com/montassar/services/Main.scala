package com.montassar.services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.montassar.cassandra_helpers.CassandraSessionProvider
import com.montassar.routes.PersonRoutes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App {

  val host = "0.0.0.0"
  val port = 80

  implicit val system: ActorSystem = ActorSystem("HEB-decision-support-cache")
  implicit val materializer: Materializer = Materializer(system)

  val route: Route = PersonRoutes.routes

  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.onComplete {
    case Success(_) => {
      val cassandraSession = CassandraSessionProvider.cassandraConn
      println(s"connection to cassandra is established ${cassandraSession}")
      println(s"\nServer running on $host:$port\nhit RETURN to terminate")
    }
    case Failure(error) => println(s"Failed: ${error.getMessage}")
  }

}
