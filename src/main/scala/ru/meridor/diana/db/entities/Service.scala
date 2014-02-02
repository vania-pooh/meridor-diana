package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables.{Units, ServiceGroups, Services}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession
import scala.collection.mutable

/**
 * Encapsulates service group
 */
case class ServiceGroup(id: Int, name: String, displayName: String, sequence: Int, parentGroup: Option[ServiceGroup]) extends Ordered[ServiceGroup]{

  def getChildGroups = ServiceGroup.getByParentGroup(Some(this))

  def compare(that: ServiceGroup): Int = displayName.compareTo(that.displayName)

  override def hashCode() = toString.hashCode()

  override def equals(obj: scala.Any) = obj.isInstanceOf[ServiceGroup] && obj.asInstanceOf[ServiceGroup].id.equals(id)

  override def toString = "ServiceGroup(" + id + ", " + displayName + ")"
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
case class UnitOfMeasure(id: Int, displayName: String){

  override def hashCode() = toString.hashCode

  override def toString = "UnitOfMeasure(" + id + ", " + displayName + ")"

}

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

case class ServiceGroupContents(childGroupsData: ServicesData, services: List[Service]) extends Iterable[Service]{

  private lazy val list = toList

  def ++(serviceGroupContents: ServiceGroupContents) = new ServiceGroupContents(
    childGroupsData ++ serviceGroupContents.childGroupsData,
    services ::: serviceGroupContents.services ::: Nil
  )

  def shuffle: ServiceGroupContents = {
    val shuffledChildGroupsData = childGroupsData map { entry => entry._1 -> entry._2.shuffle }
    val shuffledServices = scala.util.Random.shuffle(services)
    new ServiceGroupContents(shuffledChildGroupsData, shuffledServices)
  }

  /**
   * Takes n elements from services section
   * @param n
   * @return
   */
  override def take(n: Int) = new ServiceGroupContents(ServicesData.empty, services.take(n))

  /**
   * Flattens services from all child groups to the top level
   * @return
   */
  def flatten: ServiceGroupContents = new ServiceGroupContents(
    ServicesData.empty,
    (childGroupsData flatMap { entry => entry._2.flatten.services}).toList ::: services ::: Nil
  )

  def iterator = list.iterator

  override def toList: List[Service] = {
    val ret = scala.collection.mutable.ArrayBuffer[Service]()
    childGroupsData foreach {
      el => ret ++= el._2.toList
    }
    ret ++= services
    ret.toList
  }

  override def toString() = "ServiceGroupContents(" + childGroupsData + ", " + services + ")"
}

object ServiceGroupContents {

  def empty = new ServiceGroupContents(ServicesData.empty, List[Service]())

}

//TODO: find the way to parameterize Slick queries because data retrieval logic is the same for all queries
object Service{
  
  def getById(id: Long): Option[Service] = {
    val services = getByIds(List(id))
    if (services.length > 0)
      Some(services(0))
      else None
  }

  def getByIds(ids: List[Long]): List[Service] = {
    DB withSession {
      val rawRecords = for {
        s <- Services if s.serviceId inSetBind ids
      } yield (s.serviceId, s.serviceName, s.price, s.unitId, s.groupId)
      val records = rawRecords.list
      if (records.size > 0)
        records map (r => new Service(r))
        else List[Service]()
    }
  }

  def getByGroups(groupNames: List[String]): ServicesData = {
    import scala.collection.immutable.TreeMap
    if (groupNames.size > 0){
      DB withSession {
        val rawRecords = for {
          s <- Services
          g <- s.fkServicesServiceGroups if g.groupName inSetBind groupNames
        } yield (s.serviceId, s.serviceName, s.price, s.unitId, s.groupId)
        val records = rawRecords.sortBy(_._2.asc).list
        val services = if (records.size > 0)
          records map (r => new Service(r))
          else List[Service]()
        val map = scala.collection.mutable.Map[ServiceGroup, ServiceGroupContents]()
        val groups = (groupNames map(g => ServiceGroup.getByName(g))).flatten
        for (group <- groups){
          val groupServices = services filter (_.group == group)
          val childGroupNames = group.getChildGroups map (_.name)
          map += (group -> ServiceGroupContents(getByGroups(childGroupNames), groupServices))
        }
        TreeMap[ServiceGroup, ServiceGroupContents](map.toSeq:_*)
      }
    } else ServicesData.empty
  }

  /**
   * Returns randomized services data taking no more than limit entries per group
   * @param groupNames
   * @param limit
   * @return
   */
  def getRandom(groupNames: List[String], limit: Int): ServicesData =
    if (limit > 0) {
      val randomizedServices: Map[ServiceGroup, ServiceGroupContents] =
        getByGroups(groupNames) map {
          entry => entry._1 -> entry._2.flatten.shuffle
        }
      randomizedServices map (entry => entry._1 -> entry._2.take(limit))
    } else ServicesData.empty

  def getByGroup(groupName: String): ServiceGroupContents = {
    val map: Map[ServiceGroup, ServiceGroupContents] = getByGroups(List(groupName))
    if (map.size > 0) map(map.head._1) else ServiceGroupContents.empty
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
  }

  /**
   * Merges all input services data to the single services data with service group name = outputServiceGroupName
   * @param outputServiceGroupName
   * @param servicesData
   * @return
   */
  def merge(outputServiceGroupName: String, servicesData: ServicesData*): ServicesData = ServiceGroup.getByName(outputServiceGroupName) match {
    case Some(serviceGroup) => {
      val allServiceGroupContents: List[ServiceGroupContents] = List(servicesData:_*) flatMap (sd => sd map (_._2))
      val mergedServiceGroupContents: ServiceGroupContents = (allServiceGroupContents.tail foldLeft allServiceGroupContents.head)(_ ++ _)
      Map(serviceGroup -> mergedServiceGroupContents)
    }
    case None => ServicesData.empty
  }

}

object ServicesData {

  def empty = Map[ServiceGroup, ServiceGroupContents]()

  def fromList(services: List[Service], parentGroups: List[ServiceGroup] = ServiceGroup.topGroups): ServicesData = {
    //Group service by their service group
    val servicesByGroup = mutable.HashMap[ServiceGroup, mutable.ArrayBuffer[Service]]()
    services foreach {
      service => {
        val group = service.group
        if (servicesByGroup.contains(group))
          servicesByGroup(group) += service
          else servicesByGroup += group -> mutable.ArrayBuffer(service)
      }
    }

    //Build recursive structure
    val ret = mutable.HashMap[ServiceGroup, ServiceGroupContents]()
    parentGroups foreach {
      gr => {
        val ownServices = if (servicesByGroup.contains(gr)) servicesByGroup(gr).toList else List[Service]()
        val childGroups = gr.getChildGroups
        val childServices = mutable.ArrayBuffer[Service]()
        childGroups foreach {
          cg => if (servicesByGroup.contains(cg)){
            childServices ++= servicesByGroup(cg)
          }
        }

        val childGroupContents = if ( (childGroups.length > 0) && (childServices.length > 0) )
          fromList(childServices.toList, childGroups)
          else empty
        if ( (childGroupContents.size > 0) || (ownServices.length > 0) ){
          ret += gr -> ServiceGroupContents(childGroupContents, ownServices)
        }
      }
    }
    ret.toMap

  }

}