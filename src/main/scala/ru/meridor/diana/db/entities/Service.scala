package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables.Services
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession

/**
 * Encapsulates service group
 */
case class ServiceGroup(id: Int, name: String, displayName: String, sequence: Int)

/**
 * Encapsulates a single unit of measure, like meters, barrels, inches, etc.
 */
case class UnitOfMeasure(id: Int, displayName: String)

/**
 * A single service (i.e. named action with unit of measure and price)
 */
case class Service(id: Long, displayName: String, price: Double, unitOfMeasure: UnitOfMeasure, group: ServiceGroup){

  private def this(data: (Long, String, Double, Int, String, Int, String, String, Int)) =
    this(
      data._1,
      data._2,
      data._3,
      new UnitOfMeasure(data._4, data._5),
      new ServiceGroup(data._6, data._7, data._8, data._9)
    )
}

//TODO: find the way to parametrize Slick queries because data retrieval logic is the same for all queries
object Service{

  def getById(id: Long): Option[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.serviceId === id
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.displayName, g.sequence)
      val records = rawRecords.list
      return if (records.size > 0)
        Some(new Service(records(0)))
        else None
    }
    None
  }

  def getByGroups(groupNames: List[String]): Map[ServiceGroup, List[Service]] = {
    DB withSession {
      val rawRecords = for {
        s <- Services
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups if g.groupName inSetBind groupNames
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.displayName, g.sequence)
      val records = rawRecords.list
      if (records.size > 0){
        val map = scala.collection.mutable.Map[ServiceGroup, List[Service]]()
        val services = records map (r => new Service(r))
        val groups = services.map(r => r.group).distinct
        for (group <- groups){
          map += (group -> (services filter (_.group == group)) )
        }
        return map.toMap[ServiceGroup, List[Service]]
      }
    }
    Map[ServiceGroup, List[Service]]()
  }

  def getByGroup(groupName: String): List[Service] = {
    val map = getByGroups(List(groupName))
    if (map.size > 0) map(map.head._1) else List[Service]()
  }

  def getByUnitOfMeasure(unitOfMeasure: UnitOfMeasure): List[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.unitId === unitOfMeasure.id
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.displayName, g.sequence)
      val records = rawRecords.list
      return if (records.size > 0)
        records.map(r => new Service(r))
      else Nil
    }
    Nil
  }

}