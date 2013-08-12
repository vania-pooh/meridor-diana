package ru.meridor.diana.notification.strategies

import java.util.{Date, Properties}
import ru.meridor.diana.util.JSONPostRequestSupport
import ru.meridor.diana.notification.entities.SMSNotification
import org.joda.time.DateTime

/**
 * Encapsulates http://iqsms.ru/ JSON API for sending SMS
 */
class IQSMSGate extends JSONPostRequestSupport {
  /**
   * Stores SMS notification properties
   */
  private val properties = {
    val props = new Properties
    props.load(getClass.getResourceAsStream("/iqsms.properties"))
    props
  }

  private def getBaseUrl(): String = getProperty("sms.url")
  private def getLogin(): String = getProperty("sms.login")
  private def getPassword(): String = getProperty("sms.password")
  private def getProperty(name: String): String = if (properties.get(name) != null)
    properties.get(name).toString
  else ""

  private def canProcessRequests() = (getBaseUrl().size > 0) && (getLogin().size > 0) && (getPassword().size > 0)

  private def getUrl(uri: String): String = getBaseUrl() + "/" + uri + "/"

  private def getDefaultRequestParameters(): Map[String, Any] =
    Map[String, Any]("login" -> getLogin(), "password" -> getPassword())

  private def isRequestSuccessful(response: Map[String, Any]): Boolean = response("status") match {
    case Some(status) => (status == "ok")
    case None => false
  }

  /**
   * Sends a list of messages
   * @param messages
   * @param statusQueueName
   * @param scheduleTime
   * @return
   */
  def sendMessages(
      messages: List[SMSNotification],
      statusQueueName: String = "defaultMessagesQueue",
      scheduleTime: Option[Date] = None
  ): Boolean =
    if (messages.size > 0) {
      val requestParameters = scala.collection.mutable.Map() ++ getDefaultRequestParameters()
      requestParameters ++ Set("statusQueueName" -> statusQueueName)
      scheduleTime match {
        case Some(time) => requestParameters ++ Set("scheduleTime" -> new DateTime(time).toString())
        case None => ()
      }
      for (message <- messages){
        val messageParameters = scala.collection.mutable.Set(
          "clientId" -> message.getId,
          "phone" -> message.getPhone,
          "text" -> message.getMessage,
          "sender" -> message.getSender
        )
        if (message.isFlash){
          messageParameters ++ Seq("flash" -> 1)
        }
      }
      val response = sendRequest(getUrl("send"), requestParameters.toMap[String, Any])
      return true
    } else false

  /**
   * Returns total amount of money on account
   * @return
   */
  def getAccountBalance(): Double = {
    val response = sendRequest(getUrl("credits"), getDefaultRequestParameters())
    response.size match {
      case 2 => if (isRequestSuccessful(response))
        response("credits").asInstanceOf[Double] else 0d
      case _ => 0d
    }
  }

  /**
   * Returns a list of available message senders
   * @return
   */
  def getSenders(): List[String] = {
    val response = sendRequest(getUrl("senders"), getDefaultRequestParameters())
    response.size match {
      case 2 => if (isRequestSuccessful(response))
        response("senders").asInstanceOf[List[String]] else List.empty[String]
      case _ => List.empty[String]
    }
  }

}
