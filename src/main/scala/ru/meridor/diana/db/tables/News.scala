/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object News extends Table[(Timestamp, String)]("news") {
  def date = column[Timestamp]("date", O.NotNull)
  def markdown = column[String]("markdown", O.NotNull)
  def * = date ~ markdown
  def pk = primaryKey("news_pkey", (date))
  def newsPkey = index("news_pkey", (date), unique = true)
}
