package ru.meridor.diana.db.entities

import ru.meridor.diana.db.tables.{Persons, Contacts}
import ru.meridor.diana.db.DB
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession
import java.sql.Timestamp
import java.util.Date

case class Contact(
    id: Option[Long] = None,
    contactType: ContactType.ContactType = ContactType.Person,
    numRequests: Int = 1, //We create contact on first request
    created: Timestamp = new Timestamp(new Date().getTime)
)

object ContactType extends Enumeration {
  type ContactType = Value
  val Person = Value(0)
  val Organization = Value(1)
  def fromNumber(number: Int): ContactType = number match {
    case 0 => Person
    case 1 => Organization
  }
}

object Contact extends Entity[Contact, Long]{


  /**
   * Returns unique identifier of an entity
   * @param contact
   * @return
   */
  def id(contact: Contact): Long = contact.id match {
    case Some(id) => id
    case None => 0
  }

  /**
   * Searches for an entity by its contactId
   * @param contactId
   * @return None if not exists, Some(entity instance) if exists
   */
  def exists(contactId: Long): Option[Contact] = {
    try{
      DB withSession {
        val rawRecords = for {
          c <- Contacts if c.contactId === contactId
        } yield c.*
        val records = rawRecords.list
        records.size match {
          case 0 => None
          case _ => {
            val record = records(0)
            Some(new Contact(
              Some(record._1),
              ContactType.fromNumber(record._2),
              record._3,
              record._4
            ))
          }
        }
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        None
      }
    }
  }

  /**
   * Creates a new entity in the database
   * @param contact
   * @return None on failure and Some(entity) on success
   */
  def create(contact: Contact): Option[Contact] = {
    try{
      DB withSession{
        val contactId = Contacts.withAutoInc.insert((contact.contactType.id, contact.numRequests, contact.created))
        Some(new Contact(Some(contactId), contact.contactType, contact.numRequests, contact.created))
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        None
      }
    }
  }

  /**
   * Updates entity columns
   * @param contact an entity with updated fields
   * @return
   */
  def update(contact: Contact): Option[Contact] = Some(contact)

}

case class Person(
  contact: Option[Contact] = None,
  firstName: String,
  middleName: Option[String] = None,
  lastName: Option[String] = None,
  position: Option[String] = None,
  cellPhone: Long,
  fixedPhone: Option[Long] = None,
  passport: Option[String] = None,
  address: Option[String] = None,
  district: Option[String] = None,
  age: Option[Int] = None,
  profession: Option[String] = None,
  averagePayment: Option[Double] = None,
  misc: Option[String] = None
)

object Person extends Entity[Person, Long]{


  /**
   * Returns unique identifier of an entity
   * @param person
   * @return
   */
  def id(person: Person): Long = person.cellPhone

  /**
   * Searches for an entity by its contactId
   * @param cellPhone
   * @return None if not exists, Some(entity instance) if exists
   */
  def exists(cellPhone: Long): Option[Person] = {
    try{
      DB withSession {
        val rawRecords = for {
          p <- Persons if p.cellPhone === cellPhone
        } yield p.*
        val records = rawRecords.list
        records.size match {
          case 0 => None
          case _ => {
            val record = records(0)
            Contact.exists(record._1) match {
              case Some(contact) => Some(new Person(
                Some(contact),
                record._2,
                record._3,
                record._4,
                record._5,
                record._6,
                record._7,
                record._8,
                record._9,
                record._10,
                record._11,
                record._12,
                record._13,
                record._14
              ))
              case None => None
            }
          }
        }
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        None
      }
    }
  }

  /**
   * Creates a new entity in the database
   * @param person
   * @return None on failure and Some(entity) on success
   */
  def create(person: Person): Option[Person] = {
    try{
      DB withTransaction {
        val contact = Contact.create(new Contact())
        contact match {
          case Some(ct) => ct.id match {
            case Some(id) => {
              Persons.insert((
                id,
                person.firstName,
                person.middleName,
                person.lastName,
                person.position,
                person.cellPhone,
                person.fixedPhone,
                person.passport,
                person.address,
                person.district,
                person.age,
                person.profession,
                person.averagePayment,
                person.misc
              ))

              Some(new Person(
                Some(ct),
                person.firstName,
                person.middleName,
                person.lastName,
                person.position,
                person.cellPhone,
                person.fixedPhone,
                person.passport,
                person.address,
                person.district,
                person.age,
                person.profession,
                person.averagePayment,
                person.misc
              ))
            }
            case None => None
          }
          case None => None
        }
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        None
      }
    }
  }

  /**
   * Updates entity columns
   * @param person an entity with updated fields
   * @return
   */
  def update(person: Person): Option[Person] = Some(person)

}