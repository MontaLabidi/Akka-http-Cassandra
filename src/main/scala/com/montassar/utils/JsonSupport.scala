package com.montassar.utils

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.montassar.models._
import spray.json._

import scala.util.Try

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with NullOptions {

  /**
   * HANDLE UUID FORMAT IN A JSON OBJECT
   */
  implicit object UUIDSerializer extends RootJsonFormat[UUID] {
    def write(uuid: UUID): JsValue = if (uuid == null) JsString("") else JsString(uuid.toString)

    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ =>
          throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  /**
   * HANDLE DATE FORMAT IN A JSON FORMAT
   */
  implicit object DateSerializer extends RootJsonFormat[Date] {

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Convert string to date
     * @param date: the string date to convert to java.util.Date
     * @return java.util.Date
     */
    def dateParser(date: String): Date = {
      val localDate = LocalDate.parse(date, dateFormatter)
      val defaultZoneId = ZoneId.systemDefault
      Date.from(localDate.atStartOfDay(defaultZoneId).toInstant)
    }
    def write(date: Date): JsValue = {
      JsString(String.format("%1$tY-%1$tm-%1$td", date))}

    def read(json: JsValue): Date = json match {
      case JsString(rawDate) =>
        Try {
          dateParser(rawDate)
        }.toOption.fold(
            deserializationError(s"Expected ISO Date format, got $rawDate")
          )(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }
  }

  implicit val personFormat: RootJsonFormat[Person] = jsonFormat5(Person.apply)
  implicit val partialPersonFormat: RootJsonFormat[PartialPerson] = jsonFormat4(PartialPerson.apply)
}

