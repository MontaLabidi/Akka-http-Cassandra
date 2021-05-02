package com.montassar.services

import com.montassar.cassandra_helpers.CassandraAsyncQuery._
import com.montassar.models.{PartialPerson, Person}
import java.util.UUID
import java.util.UUID.randomUUID

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PersonService {

  /**
   * Query for all people in the db
   *
   * @return all people in the db
   */
  def allPersons: Future[List[Person]] = {
    execute(cql"SELECT * FROM person")
      .map(_.asScala.view.map(Person.parseEntity).toList)

  }

  /**
   * Query for a single person
   *
   * @param id : the id of the user
   * @return the person with the corresponding id if it exists, else None
   */
  def singlePerson(id: UUID): Future[Option[Person]] = {
    execute(cql"SELECT * FROM person Where id = ?", id)
      .map(_.asScala.map(Person.parseEntity).headOption)
  }

  /**
   * Query to add a person
   *
   * @param person: the new person details to add
   * @return the person with the corresponding id if existing, else None
   */
  def addPerson(person: PartialPerson): Future[Person] = {
    val new_person: Person =
      Person(
        randomUUID(),
        person.name,
        person.age,
        person.birth_date,
        person.job_title
      )
    execute(cql"INSERT INTO Person (id, name, age, birth_date, job_title) VALUES (?, ?, ?, ?, ?)",
      new_person.id,
      new_person.name,
      new_person.age,
      new_person.birth_date,
      new_person.job_title.orNull
    ).map(_ => new_person)
  }


  /**
   * Query to add a person
   *
   * @param person: the person details to change
   * @return the person with the corresponding id if existing, else None
   */
  def updatePerson(id: UUID, person: PartialPerson): Future[Person] = {
    val updated_person: Person =
      Person(
        id,
        person.name,
        person.age,
        person.birth_date,
        person.job_title
      )
    execute(cql"UPDATE Person set name = ?, age = ?, birth_date = ?, job_title = ? WHERE id = ?",
      updated_person.name,
      updated_person.age,
      updated_person.birth_date,
      updated_person.job_title.orNull,
      updated_person.id
    ).map(_ => updated_person)
  }

  /**
   * Delete person by id
   *
   * @param id : the id of the person to delete
   * @return
   */
  def deletePerson(id: Int): Future[String] = {
    execute(cql"DELETE FROM Person WHERE id = ?",
      id
    ).map(_ => "Deleted person with id $id")
  }

}
