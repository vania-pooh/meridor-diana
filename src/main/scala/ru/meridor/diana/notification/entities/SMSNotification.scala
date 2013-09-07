package ru.meridor.diana.notification.entities

import ru.meridor.diana.notification.NotificationType._

/**
 * SMS notification record
 */
case class SMSNotification (id: Long, phone: Long, sender: String = "Diana CRM", message: String) extends Notification{
  override def getType: NotificationType = SMS
}
