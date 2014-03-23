package ru.meridor.diana.notification.entities

import ru.meridor.diana.notification.NotificationType._
import ru.meridor.diana.event.{Event, EventSupport}

/**
 * SMS notification record
 */
case class SMSNotification(id: Long, phone: Long, sender: String = "Diana CRM", message: String, supportsLowFundsEvent: Boolean = false)
  extends Notification with EventSupport {

  override def getType: NotificationType = SMS

  def supportedEvents = List(LowFundsEvent)

}

object LowFundsEvent extends Event[Double]
