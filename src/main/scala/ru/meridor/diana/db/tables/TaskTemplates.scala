/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object TaskTemplates extends Table[(Long, String, String, Int, Int, Int)]("task_templates") {
  def templateId = column[Long]("template_id", O.NotNull, O.AutoInc)
  def templateName = column[String]("template_name", O.NotNull)
  def description = column[String]("description", O.NotNull)
  def categoryId = column[Int]("category_id", O.NotNull)
  def priorityId = column[Int]("priority_id", O.NotNull)
  def statusId = column[Int]("status_id", O.NotNull)
  def * = templateId ~ templateName ~ description ~ categoryId ~ priorityId ~ statusId
  def fkTaskTemplatesTaskCategories = foreignKey("fk_task_templates_task_categories", (categoryId), TaskCategories)(t => (t.categoryId))
  def fkTaskTemplatesTaskStatuses = foreignKey("fk_task_templates_task_statuses", (statusId), TaskStatuses)(t => (t.statusId))
  def fkTaskTemplatesTaskPriorities = foreignKey("fk_task_templates_task_priorities", (priorityId), TaskPriorities)(t => (t.priorityId))
  def idxTaskTemplatesTaskPriorities = index("idx_task_templates_task_priorities", (priorityId))
  def idxTaskTemplatesTaskCategories = index("idx_task_templates_task_categories", (categoryId))
  def taskTemplatesPkey = index("task_templates_pkey", (templateId), unique = true)
  def idxTaskTemplatesTaskStatuses = index("idx_task_templates_task_statuses", (statusId))
}
