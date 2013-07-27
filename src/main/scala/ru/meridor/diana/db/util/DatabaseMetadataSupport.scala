package ru.meridor.diana.db.util

import ru.meridor.diana.db.{ConnectionPooler, ConnectionPoolerSupport}
import java.sql.{Connection, DatabaseMetaData}
import scala.collection.mutable.{ListBuffer, Map, LinkedList}

/**
 * Adds support for retrieving database metadata
 */
trait DatabaseMetadataSupport extends ConnectionPoolerSupport {
  /**
   * Stores database connection instance
   */
  private var connection: Connection = null

  protected def getTablesMetadata(tablesList: List[String]): Map[String, (List[DbColumn], PrimaryKey, List[ForeignKey], List[Index])] = {
    val ret = Map[String, (List[DbColumn], PrimaryKey, List[ForeignKey], List[Index])]()

    for (tableName <- tablesList){
      ret += (
        tableName ->
          (
            getColumnsList(tableName),
            getPrimaryKey(tableName),
            getForeignKeysList(tableName),
            getIndexesList(tableName)
          )
        )
    }
    ConnectionPooler.closeConnection(connection)
    return ret
  }

  protected def getTablesList(excludedTablesList: List[String]): List[String] = {
    val ret = ListBuffer[String]()
    val tablesListResult = getDatabaseMetadata().getTables(null, null, "%", Array[String]("TABLE"))
    while (tablesListResult.next()){
      val tableName = tablesListResult.getString("TABLE_NAME")
      ret ++= (if (!excludedTablesList.contains(tableName)) List(tableName) else Nil)
    }
    ConnectionPooler.closeConnection(connection)
    return ret.toList
  }

  private def getColumnsList(tableName: String): List[DbColumn] = {
    val columns = ListBuffer[DbColumn]()
    val columnsResultSet = getDatabaseMetadata().getColumns(null, null, tableName, null)
    while(columnsResultSet.next()){
      val columnName = columnsResultSet.getString("COLUMN_NAME")
      val dataType = columnsResultSet.getInt("DATA_TYPE")
      val dataTypeName = columnsResultSet.getString("TYPE_NAME")
      val size = columnsResultSet.getInt("COLUMN_SIZE")
      val nullable = "YES".equalsIgnoreCase(columnsResultSet.getString("IS_NULLABLE"))
      val autoIncrement = "YES".equalsIgnoreCase(columnsResultSet.getString("IS_AUTOINCREMENT"))
      val defaultValue = columnsResultSet.getObject("COLUMN_DEF")
      columns += new DbColumn(columnName, dataType, dataTypeName, size, nullable, autoIncrement, defaultValue)
    }
    return columns.toList
  }

  private def getPrimaryKey(tableName: String): PrimaryKey = {
    var primaryKeyName = ""
    val primaryKeyColumns = ListBuffer[String]()
    val primaryKeyResultSet = getDatabaseMetadata().getPrimaryKeys(null, null, tableName)
    while (primaryKeyResultSet.next()){
      primaryKeyColumns += primaryKeyResultSet.getString("COLUMN_NAME")
      primaryKeyName = primaryKeyResultSet.getString("PK_NAME")
    }
    return if (primaryKeyName != null) new PrimaryKey(primaryKeyName, primaryKeyColumns.toList) else null
  }

  private def getForeignKeysList(tableName: String): List[ForeignKey] = {
    val foreignKeys = ListBuffer[ForeignKey]()
    val foreignKeysResultSet = getDatabaseMetadata().getCrossReference(null, null, null, null, null, tableName)
    val foreignKeysData = Map[String, (List[String], String, List[String])]()
    while(foreignKeysResultSet.next()){
      val name = foreignKeysResultSet.getString("FK_NAME")
      val fromColumnName = foreignKeysResultSet.getString("FKCOLUMN_NAME")
      val toTableName = foreignKeysResultSet.getString("PKTABLE_NAME")
      val toColumnName = foreignKeysResultSet.getString("PKCOLUMN_NAME")
      if (!foreignKeysData.contains(name)){
        foreignKeysData += (name -> (List[String](fromColumnName), toTableName, List[String](toColumnName)))
      }else{
        foreignKeysData.get(name).get._1 ++ fromColumnName
        foreignKeysData.get(name).get._3 ++ toColumnName
      }
    }
    for (foreignKey <- foreignKeysData){
      val foreignKeyName = foreignKey._1
      val fromColumns = foreignKey._2._1
      val toTableName = foreignKey._2._2
      val toColumns = foreignKey._2._3
      foreignKeys += new ForeignKey(foreignKeyName, fromColumns, toTableName, toColumns)
    }
    return foreignKeys.toList
  }

  private def getIndexesList(tableName: String): List[Index] = {
    val indexes = ListBuffer[Index]()
    val indexesResultSet = getDatabaseMetadata().getIndexInfo(null, null, tableName, false, false)
    val indexesData = Map[String, (ListBuffer[String], Boolean)]() //Maps index name to a tuple containing a list of columns and whether index is unique
    while(indexesResultSet.next()){
      val name = indexesResultSet.getString("INDEX_NAME")
      val unique = !indexesResultSet.getBoolean("NON_UNIQUE")
      val columnName = indexesResultSet.getString("COLUMN_NAME")
      if (!indexesData.contains(name)){
        indexesData += (name -> (ListBuffer[String](columnName), unique))
      }else{
        indexesData.get(name).get._1 += columnName //Adding a new column to the list
      }
    }
    for (index <- indexesData){
      val indexName = index._1
      val columns = index._2._1
      val unique = index._2._2
      indexes += new Index(indexName, columns.toList, unique)
    }
    return indexes.toList
  }

  private def getDatabaseMetadata(): DatabaseMetaData = {
    if ( (connection == null) || connection.isClosed ) {
      connection = ConnectionPooler.getConnection
    }
    return connection.getMetaData
  }

}
