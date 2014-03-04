package ru.meridor.diana.db.util

import ru.meridor.diana.util.{CamelCaseSupport, FileGenerationSupport}
import ru.meridor.diana.db.ConnectionPooler

/**
 * A standalone executable for generating Slick table classes from database structure
 */
object SlickTablesGenerator extends App with DatabaseMetadataSupport with FileGenerationSupport with CamelCaseSupport {

  private val excludedTables = List("schema_version")

  private val outputDirectory = "src/main/scala/ru/meridor/diana/db/tables/"

  //This actually does the work
  generateSlickTables()

  private def generateSlickTables() = {
    println("Generating tables...")
    val tablesMetadata = getTablesMetadata(getTablesList(excludedTables))
    for (tableMetadata <- tablesMetadata){
      val tableName = tableMetadata._1
      val columns = tableMetadata._2._1
      val primaryKey = tableMetadata._2._2
      val foreignKeys = tableMetadata._2._3
      val indexes = tableMetadata._2._4
      val table = new DbTable(tableName, columns, primaryKey, foreignKeys, indexes)
      println("\tGenerating table " + tableName + "...")
      reset()
      appendInfoHeader()
      appendImportStatements()
      append(table.toSlickString)
      writeToFile(outputDirectory, underscoredToCamelCase(tableName) + ".scala")
    }
    ConnectionPooler.shutdown()
  }

  private def appendImportStatements(){
    appendln("package ru.meridor.diana.db.tables")
    appendln()
    appendln("import scala.slick.driver.PostgresDriver.simple._")
    appendln("import java.sql.Timestamp")
    appendln()
  }

}
