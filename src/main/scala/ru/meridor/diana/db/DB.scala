/**
 * 
 */
package ru.meridor.diana.db

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

/**
 * An object to access Slick
 */
object DB {

  private val db = Database.forDataSource(ConnectionPooler.getDataSource)

  def withSession[T](f: => T): T = db.withSession(f)

  def withSession[T](f: Session => T): T = db.withSession(f)

  def withTransaction[T](f: => T): T = db.withTransaction(f)

  def withTransaction[T](f: Session => T): T = db.withTransaction(f)

}