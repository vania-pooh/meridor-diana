/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object TaskServices extends Table[(Long, Long, Double)]("task_services") {
  def taskId = column[Long]("task_id", O.NotNull)
  def serviceId = column[Long]("service_id", O.NotNull)
  def quantity = column[Double]("quantity", O.NotNull)
  def * = taskId ~ serviceId ~ quantity
  def idxTaskServicesServices = index("idx_task_services_services", (serviceId))
  def idxTaskServicesTasks = index("idx_task_services_tasks", (taskId))
}
