package com.montassar.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.montassar.models._
import com.montassar.utils.JsonSupport._
import com.montassar.services.PersonService._

object PersonRoutes {

  def getPersonRoutes: Route = get {
    path("persons") {
      onSuccess(allPersons) {
        case persons: List[Person] =>
          complete(persons)
        case _ =>
          complete(StatusCodes.InternalServerError)
      }
    } ~
      path("persons" / JavaUUID) { id =>
        onSuccess(singlePerson(id)) {
          case Some(person) => complete(person)
          case None         => complete("{\"message\": \"No such person!\"}")
        }
      }
  }

  def postPersonRoute: Route = post {
    path("persons") {
      entity(as[PartialPerson]) { person =>
        onSuccess(addPerson(person)) {
          case person: Person =>
            complete(person)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  def patchPersonRoute: Route = patch {
    path("persons"/ JavaUUID) { id =>
      entity(as[PartialPerson]) { person =>
        onSuccess(updatePerson(id, person)) {
          case person: Person =>
            complete(person)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }

  def deletePersonRoute: Route = delete {
        path("persons" / IntNumber) { id =>
          onSuccess(deletePerson(id)) { message =>
            complete(message)
          }
        }
      }

 val routes: Route = concat(getPersonRoutes, postPersonRoute, patchPersonRoute, deletePersonRoute)
}
