package com.montassar.models

import java.util.{Date, UUID}

import com.datastax.driver.core.Row

case class Person(id: UUID, name: String, age: Int, birth_date: Date, job_title: Option[String])
case class PartialPerson(name: String, age: Int, birth_date: Date, job_title: Option[String])

object Person {

  /**
   * Extracts Person from a Cassandra Row
   * @param row Row
   * @return MerchandiseIdLabel
   */
  def parseEntity(row: Row): Person = {
    Person(
      row.getUUID("id"),
      row.getString("name"),
      row.getInt("age"),
      row.getTimestamp("birth_date"),
      if (row.isNull("job_title")) None else Some(row.getString("job_title"))
    )
  }
}