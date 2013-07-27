package ru.meridor.diana.db.entities

import ru.meridor.diana.util.EqualsById
import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables.Services
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession

/**
 * A single service (i.e. named action with unit of measure and price)
 */
case class Service(id: Long, displayName: String, price: Double, unitOfMeasure: UnitOfMeasure, group: ServiceGroup) extends EqualsById[Service, Long]{

  private def this(data: (Long, String, Double, Int, String, Int, String, Int)) =
    this(
      data._1,
      data._2,
      data._3,
      new UnitOfMeasure(data._4, data._5),
      new ServiceGroup(data._6, data._7, data._8)
    )

  def getId = id
  def getDisplayName = displayName
  def getUnitOfMeasure = unitOfMeasure
  def getPrice = price
  def getGroup = group
}

//TODO: find the way to parametrize Slick queries because data retrieval logic is the same for all queries
object Service{

  def getById(id: Long): Option[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.serviceId === id
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.sequence)
      val records = rawRecords.list
      return if (records.size > 0)
        Some(new Service(records(0)))
        else None
    }
    None
  }

  def getByGroups(groups: List[ServiceGroup]): Map[ServiceGroup, List[Service]] = {
    val groupIds = for {group <- groups} yield group.getId
    val map = scala.collection.mutable.Map[ServiceGroup, List[Service]]()
    DB withSession {
      val rawRecords = for {
        s <- Services if s.groupId inSetBind groupIds
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.sequence)
      val records = rawRecords.list
      if (records.size > 0){
        val services = records map (r => new Service(r))
        for (group <- groups){
          map + (group -> (services filter (_.getGroup == group)) )
        }
      }
    }
    map.toMap[ServiceGroup, List[Service]]
  }

  def getByGroup(group: ServiceGroup): List[Service] = getByGroups(List(group))(group)

  def getByUnitOfMeasure(unitOfMeasure: UnitOfMeasure): List[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.unitId === unitOfMeasure.getId
        u <- s.fkServicesUnits
        g <- s.fkServicesServiceGroups
      } yield (s.serviceId, s.serviceName, s.price, u.unitId, u.displayName, g.groupId, g.groupName, g.sequence)
      val records = rawRecords.list
      return if (records.size > 0)
        records.map(r => new Service(r))
      else Nil
    }
    Nil
  }

}