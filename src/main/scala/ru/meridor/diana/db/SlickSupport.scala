/**
 * 
 */
package ru.meridor.diana.db

import scala.slick.driver.MySQLDriver.simple._

/**
 * Adds Slick query compiler support 
 */
trait SlickSupport extends BoneCPSupport {
  /**
   * Slick database instance
   */
  val db = Database.forDataSource(cpds)
}