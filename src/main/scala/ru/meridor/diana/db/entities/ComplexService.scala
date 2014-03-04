package ru.meridor.diana.db.entities

import ru.meridor.diana.db.DB
import ru.meridor.diana.db.tables._
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database.threadLocalSession

case class ComplexService(id: Long, name: String, displayName: String){

  def toServiceGroup = new ServiceGroup(0, name, displayName, 1, None)

}

/**
 * Allows to fetch complex service contents as ServicesData object
 */
object ComplexService {

  def getByName(name: String): ServicesData = {
    DB withSession {
      val rawRecords = for {
        (csc, sq) <- ComplexServiceContents leftJoin ServiceQuantities on (
          (c, s) => (c.serviceId === s.serviceId) && (c.complexServiceId === s.complexServiceId)
        )
        cs <- ComplexServices if (cs.serviceId === csc.complexServiceId) && (cs.serviceName === name)
        st <- ComplexServiceStages if st.stageId === csc.stageId
        s <- Services if s.serviceId === csc.serviceId
      } yield (
            //Sequence columns should be always on the first two places for correct sorting!
            st.sequence, csc.sequence,
            s.serviceId, st.stageId,
            cs.serviceId, cs.serviceName, cs.displayName,
            sq.quantity.?
          )
      val records = rawRecords.sortBy(_._1).sortBy(_._2).list
      if (records.size > 0){
        val firstRecord = records(0)
        val complexService = ComplexService(firstRecord._4, firstRecord._6, firstRecord._7)
        val map = scala.collection.mutable.LinkedHashMap[ServiceGroup, ServiceGroupContents]()
        val serviceIds = records map (r => r._3)
        val quantities = scala.collection.mutable.Map[Long, Float]()
        records foreach {
          r => r._8 match {
            case Some(quantity) => quantities += r._3 -> quantity.asInstanceOf[Float]
            case None => ()
          }
        }
        val services = Service.getByIds(serviceIds, quantities.toMap)
        val stageIds = records map (r => r._4)
        stageIds foreach {
          stageId => {
            val stage = ComplexServiceStage.getById(stageId)
            val stageServiceIds = records.filter(_._4 == stageId).map(_._3)
            val stageServices = services.filter(s => stageServiceIds.contains(s.id))
            map += stage.toServiceGroup -> new ServiceGroupContents(ServicesData.empty, stageServices)
          }
        }
        Map[ServiceGroup, ServiceGroupContents]() + (complexService.toServiceGroup -> new ServiceGroupContents(map.toMap, List()))
      }
      else ServicesData.empty
    }
  }

}

case class ComplexServiceStage(id: Int, name: String, displayName: String, sequence: Int){

  def this(data:(Int, String, String, Int)) = this(data._1, data._2, data._3, data._4)

  override def hashCode() = toString.hashCode

  override def toString = "ComplexServiceStage(" + id + ", " + name + ", " + displayName + ")"

  def toServiceGroup = new ServiceGroup(id, name, displayName, sequence, None)

}

object ComplexServiceStage {

  private lazy val searchHashTable = load

  private def load: Map[Int, ComplexServiceStage] = {
    DB withSession {
      val rawRecords = for {
        st <- ComplexServiceStages
      } yield (st.stageId, st.stageName, st.displayName, st.sequence)
      val records = rawRecords.list
      (records map (r => r._1 -> new ComplexServiceStage(r))).toMap[Int, ComplexServiceStage]
    }
  }

  def getById(id: Int): ComplexServiceStage = searchHashTable(id)

}