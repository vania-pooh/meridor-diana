package ru.meridor.diana.notification.entities

import ru.meridor.diana.notification.NotificationType._

/**
 * A base class for notification information object
 */
abstract class Notification {
  /**
   * Returns unique numeric notification ID
   * @return
   */
  def getId(): Int

  /**
   * Returns notification type
   * @return
   */
  def getType(): NotificationType

}