package ru.meridor.diana.notification.strategies

import ru.meridor.diana.notification.entities.Notification

/**
 * A base class for any notification strategy
 */
abstract class NotificationStrategy[+T <: Notification] {
  def doNotify[U >: T](notification: U): Boolean
}
