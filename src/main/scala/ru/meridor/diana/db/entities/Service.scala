package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables.{Units, ServiceGroups, Services}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession

/**
 * Encapsulates service group
 */
case class ServiceGroup(id: Int, name: String, displayName: String, sequence: Int, parentGroup: Option[ServiceGroup]) extends Ordered[ServiceGroup]{
  def getChildGroups = ServiceGroup.getByParentGroup(Some(this))

  def compare(that: ServiceGroup): Int = displayName.compareTo(that.displayName)
}

object ServiceGroup {

  private val groupIdHashTable = scala.collection.mutable.LinkedHashMap[Int, ServiceGroup]()
  private val groupNameHashTable = scala.collection.mutable.LinkedHashMap[String, ServiceGroup]()
  private val parentGroupHashTable = scala.collection.mutable.LinkedHashMap[Option[ServiceGroup], scala.collection.mutable.LinkedList[ServiceGroup]]()

  lazy val topGroups = loadServiceGroups

  private type ServiceGroupRecord = (Int, String, String, Int, Option[Int])

  private def initGroup(recordsMap: Map[Int, ServiceGroupRecord], groupId: Int): Option[ServiceGroup] = {
    val record = recordsMap(groupId)
    val id = record._1
    val name = record._2
    val displayName = record._3
    val sequence = record._4
    val parentGroupId = record._5
    val parentServiceGroup: Option[ServiceGroup] = parentGroupId match {
      case Some(gid) => {
        groupIdHashTable get gid match {
          case Some(psg) => Some(psg)
          case None => initGroup(recordsMap, gid)
        }
      }
      case None => None
    }
    val serviceGroup = new ServiceGroup(id, name, displayName, sequence, parentServiceGroup)
    groupIdHashTable += (id -> serviceGroup)
    groupNameHashTable += (name -> serviceGroup)
    parentGroupHashTable get parentServiceGroup match {
      case None => parentGroupHashTable put(parentServiceGroup, scala.collection.mutable.LinkedList[ServiceGroup](serviceGroup))
      case _ => parentGroupHashTable(parentServiceGroup) :+= serviceGroup
    }
    serviceGroup.parentGroup match {
      case None => Some(serviceGroup) //We return only top level groups
      case _ => None
    }
  }

  private def loadServiceGroups: List[ServiceGroup] = {
    DB withSession {
      val rawRecords = for {
        sg <- ServiceGroups
      } yield (sg.groupId, sg.groupName, sg.displayName, sg.sequence, sg.parentGroupId)
      val records = rawRecords.sortBy(_._5.nullsFirst).list
      val recordsMap = (records map (r => r._1 -> r)).toMap
      (records map (r => initGroup(recordsMap, r._1))).flatten
    }
  }

  def getById(id: Int): ServiceGroup = groupIdHashTable(id)

  def getByName(name: String): Option[ServiceGroup] = groupNameHashTable get name
  
  def getByParentGroup(parentGroup: Option[ServiceGroup]) =
    if (parentGroupHashTable.contains(parentGroup))
      parentGroupHashTable(parentGroup).toList
      else List[ServiceGroup]()
}

/**
 * Encapsulates a single unit of measure, like meters, barrels, inches, etc.
 */
case class UnitOfMeasure(id: Int, displayName: String)

object UnitOfMeasure {

  private val searchHashTable = loadUnitsOfMeasure

  private def loadUnitsOfMeasure: Map[Int, UnitOfMeasure] = {
    DB withSession {
      val rawRecords = for {
        u <- Units
      } yield (u.unitId, u.displayName)
      val records = rawRecords.list
      (records map (r => r._1 -> new UnitOfMeasure(r._1, r._2))).toMap[Int, UnitOfMeasure]
    }
  }

  def getById(id: Int): UnitOfMeasure = searchHashTable(id)

}

/**
 * A single service (i.e. named action with unit of measure and price)
 */
case class Service(id: Long, displayName: String, price: Double, unitOfMeasure: UnitOfMeasure, group: ServiceGroup){

  def this(data: (Long, String, Double, Int, Int)) =
    this(
      data._1,
      data._2,
      data._3,
      UnitOfMeasure.getById(data._4),
      ServiceGroup.getById(data._5)
    )
}

case class ServiceGroupContents(childGroupsData: Map[ServiceGroup, ServiceGroupContents], services: List[Service])

//TODO: find the way to parametrize Slick queries because data retrieval logic is the same for all queries
object Service{

  def getById(id: Long): Option[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.serviceId === id
      } yield (s.serviceId, s.serviceName, s.price, s.unitId, s.groupId)
      val records = rawRecords.list
      if (records.size > 0)
        Some(new Service(records(0)))
        else None
    }
    None
  }

  def getByGroups(groupNames: List[String]): Map[ServiceGroup, ServiceGroupContents] = {
    import scala.collection.immutable.TreeMap
    if (groupNames.size > 0){
      DB withSession {
        val rawRecords = for {
          s <- Services
          g <- s.fkServicesServiceGroups if g.groupName inSetBind groupNames
        } yield (s.serviceId, s.serviceName, s.price, s.unitId, s.groupId)
        val records = rawRecords.sortBy(_._2.asc).list
        if (records.size > 0){
          val map = scala.collection.mutable.Map[ServiceGroup, ServiceGroupContents]()
          val services = records map (r => new Service(r))
          val groups = (groupNames map(g => ServiceGroup.getByName(g))).flatten
          for (group <- groups){
            val groupServices = services filter (_.group == group)
            val childGroupNames = group.getChildGroups map (_.name)
            map += (group -> ServiceGroupContents(getByGroups(childGroupNames), groupServices))
          }
          TreeMap[ServiceGroup, ServiceGroupContents](map.toSeq:_*)
        } else Map[ServiceGroup, ServiceGroupContents]()
      }
    } else Map[ServiceGroup, ServiceGroupContents]()
  }

  def getByGroup(groupName: String): ServiceGroupContents = {
    val map = getByGroups(List(groupName))
    if (map.size > 0) map(map.head._1) else emptyServiceGroupContents
  }

  def getByUnitOfMeasure(unitOfMeasure: UnitOfMeasure): List[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.unitId === unitOfMeasure.id
      } yield (s.serviceId, s.serviceName, s.price, s.unitId, s.groupId)
      val records = rawRecords.list
      if (records.size > 0)
        records.map(r => new Service(r))
        else Nil
    }
    Nil
  }

  private def emptyServiceGroupContents = ServiceGroupContents(Map[ServiceGroup, ServiceGroupContents](), List[Service]())

}