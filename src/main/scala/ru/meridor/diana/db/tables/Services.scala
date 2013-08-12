/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object Services extends Table[(Long, String, Int, Double, Int)]("services") {
  def serviceId = column[Long]("service_id", O.NotNull, O.AutoInc)
  def serviceName = column[String]("service_name", O.NotNull)
  def unitId = column[Int]("unit_id", O.NotNull)
  def price = column[Double]("price", O.NotNull)
  def groupId = column[Int]("group_id")
  def * = serviceId ~ serviceName ~ unitId ~ price ~ groupId
  def withAutoInc = serviceName ~ unitId ~ price ~ groupId returning serviceId
  def onlyRequired = serviceId ~ serviceName ~ unitId ~ price ~ groupId.?
  def pk = primaryKey("services_pkey", (serviceId))
  def fkServicesUnits = foreignKey("fk_services_units", (unitId), Units)(t => (t.unitId))
  def fkServicesServiceGroups = foreignKey("fk_services_service_groups", (groupId), ServiceGroups)(t => (t.groupId))
  def servicesPkey = index("services_pkey", (serviceId), unique = true)
  def idxServicesUnits = index("idx_services_units", (unitId))
  def idxServiceServiceGroups = index("idx_service_service_groups", (groupId))
}
