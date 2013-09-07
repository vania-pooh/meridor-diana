package ru.meridor.diana.util

import java.util.Properties

/**
 * Gives a simple interface to *.properties file
 */
trait PropertiesFileSupport {
  /**
   * Stores SMS notification properties
   */
  private lazy val properties: Option[Properties] = {
    val props = new Properties
    try{
      props.load(getClass.getResourceAsStream(propertiesFileName))
      Some(props)
    } catch {
      case e: Exception => {
        e.printStackTrace()
        None
      }
    }
  }

  protected def propertiesFileName: String

  protected def getProperty(name: String): Option[String] = {
    properties match {
      case Some(pr) => if (pr.get(name) != null)
        Some(pr.get(name).toString)
        else None

      case None => None
    }
  }

}
