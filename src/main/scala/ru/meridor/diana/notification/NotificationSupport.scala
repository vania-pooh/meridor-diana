package ru.meridor.diana.notification

import ru.meridor.diana.notification.strategies.NotificationStrategyFactory
import ru.meridor.diana.notification.entities.Notification

/**
 * A base interface for any notification subsystem. notify() is reserved name for java.lang.Object method.
 */
trait NotificationSupport {
  /**
   * Notifies user about any events in the system
   * @param notification an object with notification information
   * @return whether notification was sent successfully
   */
  def sendNotification[U <: Notification](notification: U) : Boolean = {
    if (notification == null) false
      else NotificationStrategyFactory.get(notification).doNotify(notification)
  }
}
