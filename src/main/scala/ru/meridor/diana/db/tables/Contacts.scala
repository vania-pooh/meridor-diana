/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object Contacts extends Table[(Long, Timestamp, Int, Timestamp)]("contacts") {
  def contactId = column[Long]("contact_id", O.NotNull, O.AutoInc)
  def contactType = column[Timestamp]("contact_type", O.NotNull)
  def numRequests = column[Int]("num_requests", O.NotNull)
  def created = column[Timestamp]("created", O.NotNull)
  def * = contactId ~ contactType ~ numRequests ~ created
  def contactsPkey = index("contacts_pkey", (contactId), unique = true)
}
