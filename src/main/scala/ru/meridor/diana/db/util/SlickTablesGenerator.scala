package ru.meridor.diana.db.util

import ru.meridor.diana.db.BoneCPSupport
import java.sql.{Types, DatabaseMetaData}
import ru.meridor.diana.util.ClassGenerationSupport

/**
 * A standalone executable for generating Slick table classes from database structure
 */
object SlickTablesGenerator extends App with BoneCPSupport with ClassGenerationSupport {

  /**
   * Stores database connection instance
   */
  private val connection = cpds.getConnection

  //This actually does the work
  generateSlickTables()

  private def generateSlickTables() = {
    val metadata: DatabaseMetaData = getTablesMetadata()
//    for (tableName <- metadata.getTables(null, null, null, null)){
//      val result = metadata.getColumns(null, null, tableName, null)
//      while (result.next) {
//        val column: Nothing = new Nothing
//        column.setName(result.getString("COLUMN_NAME"))
//        column.setSqlType(result.getInt("DATA_TYPE"))
//        column.setSize(result.getInt("COLUMN_SIZE"))
//        column.setSqlTypeName(result.getString("TYPE_NAME"))
//        column.setNullable("YES".equalsIgnoreCase(result.getString("IS_NULLABLE")))
//        tableScheme.put(result.getString("COLUMN_NAME"), column)
//      }
//    }
    closeConnection(connection)
  }

  private def getTablesMetadata(): DatabaseMetaData = {
    return connection.getMetaData
  }

  /**
   * @see <a href="http://dev.mysql.com/doc/refman/5.1/en/connector-j-reference-type-conversions.html">Table 21.25. MySQL Types to Java Types</a>
   */
  protected def getColumnJavaType(sqlType: Int, size: Int): String = {
    sqlType match {
      case Types.BIGINT => "Long"
      case Types.INTEGER | Types.SMALLINT => "Int"
      case Types.TINYINT => if (size == 1) "Boolean" else "Integer"
      case Types.BIT => "Boolean"
      case Types.REAL | Types.FLOAT => "Float"
      case Types.DECIMAL | Types.DOUBLE | Types.NUMERIC => "Double"
      case Types.TIME | Types.TIMESTAMP => "Timestamp"
      case Types.DATE => "Date"
      case Types.CHAR => if (size == 1) "Character" else "String"
      case Types.LONGVARCHAR | Types.VARCHAR => "String"
      case Types.BLOB => "Blob"
      case Types.LONGVARBINARY => "Array[Byte]"
      case _ => throw new IllegalArgumentException("Unsupported MySQL type")
    }
  }
}
