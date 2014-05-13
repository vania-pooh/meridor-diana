package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables.StoredCalculations
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession

case class StoredCalculation(id: Long, displayName: String, data: String) {

  def this(data: (Long, String, String)) =
    this(
      data._1,
      data._2,
      data._3
    )

}

object StoredCalculation {

  def getById(id: Long): Option[StoredCalculation] = {
    DB withSession {
      val rawRecords = for {
        sc <- StoredCalculations if sc.storedCalculationId === id
      } yield (sc.storedCalculationId, sc.displayName, sc.data)
      val records = rawRecords.list
      if (records.size > 0)
        Some(new StoredCalculation(records(0)))
      else None
    }
  }

  def insert(displayName: String, data: String): Long = {
    DB withSession {
      StoredCalculations.withAutoInc.insert((displayName, data))
    }
  }

}
