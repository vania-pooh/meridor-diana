/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object TaskServices extends Table[(Long, Long, Double)]("task_services") {
  def taskId = column[Long]("task_id", O.NotNull)
  def serviceId = column[Long]("service_id", O.NotNull)
  def quantity = column[Double]("quantity", O.NotNull)
  def * = taskId ~ serviceId ~ quantity
  def onlyRequired = taskId ~ serviceId ~ quantity
  def pk = primaryKey("", ())
  def fkTaskServicesTasks = foreignKey("fk_task_services_tasks", (taskId), Tasks)(t => (t.taskId))
  def fkTaskServicesServices = foreignKey("fk_task_services_services", (serviceId), Services)(t => (t.serviceId))
  def idxTaskServicesServices = index("idx_task_services_services", (serviceId))
  def idxTaskServicesTasks = index("idx_task_services_tasks", (taskId))
}
