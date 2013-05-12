/**
 *
 */
package ru.meridor.diana.db

import org.scalatra.ScalatraServlet
import org.slf4j.LoggerFactory
import java.util.Properties
import com.jolbox.bonecp.BoneCPDataSource

/**
 * Delivers basic Slick and BoneCP support
 */
trait BoneCPSupport extends ScalatraServlet {
  
  private val logger = LoggerFactory.getLogger(this.getClass)

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
   * Connection pooler will be automatically shut down on servlet destroy
   */
  override def destroy() {
    super.destroy()
    shutdownConnectionPooler
  }
  
  def shutdownConnectionPooler() {
    logger.info("Shutting down BoneCP connection pool...")
    cpds.close
  }

}