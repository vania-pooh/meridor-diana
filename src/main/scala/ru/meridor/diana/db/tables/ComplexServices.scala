/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object ComplexServices extends Table[(Long, String, String)]("complex_services") {
  def serviceId = column[Long]("service_id", O.NotNull, O.AutoInc)
  def serviceName = column[String]("service_name", O.NotNull)
  def displayName = column[String]("display_name", O.NotNull)
  def * = serviceId ~ serviceName ~ displayName
  def withAutoInc = serviceName ~ displayName returning serviceId
  def pk = primaryKey("complex_services_pkey", (serviceId))
  def complexServicesPkey = index("complex_services_pkey", (serviceId), unique = true)
  def complexServicesServiceNameKey = index("complex_services_service_name_key", (serviceName), unique = true)
}
