/**
 *
 */
package ru.meridor.diana.db

import org.slf4j.LoggerFactory
import com.googlecode.flyway.core.Flyway

/**
 * Adds Flyway migrations support
 */
trait FlywaySupport extends BoneCPSupport {
    /**
     * Logger instance
     */
	private val logger = LoggerFactory.getLogger(this.getClass)
	
	/**
	 * Flyway migration instance
	 */
    private val flyway = new Flyway

    /**
     * A list of paths to be used to search for migrations
     */
    private val migrationLocations = Seq[String](
	  "db/migration/java",
	  "db/migration/sql"
    )
    
	/**
	 * Migrations will be applied during servlet initialization
	 */
	override def init(){
	  logger.info("Checking whether we need to apply new database migrations...")
	  flyway.setDataSource(cpds)
	  if (databaseNeedsMigration){
	    logger.info("Migrating database...")
	    flyway.setEncoding("UTF8")
	    flyway.setLocations(migrationLocations:_*)
	    flyway.migrate
	  } else {
	    logger.info("No database migration needed.")
	  } 
	}
	
	private def databaseNeedsMigration(): Boolean = {
	  val alreadyApplied = flyway.info().applied().size
	  val needToApply = flyway.info().pending().size
	  logger.info("Database state: previously applied " + alreadyApplied + " migrations, need to apply " + needToApply + " migrations.")
      (needToApply > 0)
	}
}