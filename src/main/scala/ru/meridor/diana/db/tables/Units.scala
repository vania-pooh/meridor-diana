/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object Units extends Table[(Int, String)]("units") {
  def unitId = column[Int]("unit_id", O.NotNull, O.AutoInc)
  def displayName = column[String]("display_name", O.NotNull)
  def * = unitId ~ displayName
  def withAutoInc = displayName returning unitId
  def onlyRequired = unitId ~ displayName
  def pk = primaryKey("units_pkey", (unitId))
  def unitsPkey = index("units_pkey", (unitId), unique = true)
}
