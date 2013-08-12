package ru.meridor.diana.notification.entities

import ru.meridor.diana.notification.NotificationType._

/**
 * SMS notification record
 */
class SMSNotification (id: Int, phone: Int, sender: String, message: String, isFlashNotification: Boolean = false, wapUrl: String = "") extends Notification{

  def getId(): Int = id
  def getType(): NotificationType = SMS
  def getPhone = phone
  def getSender = sender //That's a message sender name like "Meridor"
  def getMessage = message
  def isFlash = isFlashNotification
  def getWapUrl = wapUrl
}
