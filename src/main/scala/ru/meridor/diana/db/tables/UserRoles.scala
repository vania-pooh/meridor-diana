/***********************************************************
 * DO NOT EDIT THIS FILE - IT WAS GENERATED AUTOMATICALLY. *
 ***********************************************************/
package ru.meridor.diana.db.tables

import scala.slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

object UserRoles extends Table[(Int, Int)]("user_roles") {
  def roleId = column[Int]("role_id", O.NotNull)
  def userId = column[Int]("user_id", O.NotNull)
  def * = roleId ~ userId
  def pk = primaryKey("user_roles_pkey", (roleId, userId))
  def fkUserRolesUsers = foreignKey("fk_user_roles_users", (userId), Users)(t => (t.userId))
  def fkUserRolesRoles = foreignKey("fk_user_roles_roles", (roleId), Roles)(t => (t.roleId))
  def idxUserRolesRoles = index("idx_user_roles_roles", (roleId))
  def userRolesPkey = index("user_roles_pkey", (roleId, userId), unique = true)
  def idxUserRolesUsers = index("idx_user_roles_users", (userId))
}
