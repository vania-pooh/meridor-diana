package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession
import java.sql.Timestamp

/**
 * Stores entities related to news
 */
case class News(date: Timestamp, markdown: String){
  def this(record: (Timestamp, String)) = this(record._1, record._2)
}

object News {

  /**
   * Returns all or some last news in the table
   * @return
   */
  def get(limit: Option[Int] = None): List[News] = {
    DB withSession {
      val arr = scala.collection.mutable.ArrayBuffer[News]()
      val rawRecords = for {
        n <- ru.meridor.diana.db.tables.News
      } yield (n.date, n.markdown)
      val records = limit match {
        case Some(l) => rawRecords.sortBy(_._1.desc).take(l).list
        case None => rawRecords.sortBy(_._1.desc).list
      }
      records foreach {
        r => arr += new News(r)
      }
      arr.toList
    }
  }

}
