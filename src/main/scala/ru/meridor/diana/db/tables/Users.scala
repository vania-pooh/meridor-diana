/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object Users extends Table[(Int, String, String, Option[Long])]("users") {
  def userId = column[Int]("user_id", O.NotNull, O.AutoInc)
  def email = column[String]("email", O.NotNull)
  def password = column[String]("password", O.NotNull)
  def contactId = column[Option[Long]]("contact_id", O.Nullable)
  def * = userId ~ email ~ password ~ contactId
  def withAutoInc = email ~ password ~ contactId returning userId
  def pk = primaryKey("users_pkey", (userId))
  def fkUsersContacts = foreignKey("fk_users_contacts", (contactId), Contacts)(t => (t.contactId))
  def usersPkey = index("users_pkey", (userId), unique = true)
  def usersEmailKey = index("users_email_key", (email), unique = true)
  def idxUsersContacts = index("idx_users_contacts", (contactId))
}
