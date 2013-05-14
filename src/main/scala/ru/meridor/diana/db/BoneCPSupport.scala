/**
 *
 */
package ru.meridor.diana.db

import org.scalatra.ScalatraServlet
import org.slf4j.LoggerFactory
import java.util.Properties
import com.jolbox.bonecp.BoneCPDataSource

/**
 * Delivers basic BoneCP support
 */
trait BoneCPSupport {
  
  private val logger = LoggerFactory.getLogger(this.getClass)

  /**
   * Provides connection pooler datasource object. Call cpds.getConnection() to get JDBC connection.
   */
  val cpds = {
    logger.info("Loading database properties...")
    val props = new Properties
    props.load(getClass.getResourceAsStream("/bonecp.properties"))
    Class.forName("com.mysql.jdbc.Driver")
    val cpds = new BoneCPDataSource
    cpds.setProperties(props)
    logger.info("Initialized BoneCP connection pool.")
    cpds
  }

  /**
   * Shuts down connection pool. Is expected to be called when application finishes its work (e.g. on servlet destroy).
   */
  protected def shutdownConnectionPooler() {
    logger.info("Shutting down BoneCP connection pool...")
    cpds.close
  }

}