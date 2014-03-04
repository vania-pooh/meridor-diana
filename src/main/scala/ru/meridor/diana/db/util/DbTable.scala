package ru.meridor.diana.db.util

import ru.meridor.diana.util.FileGenerationSupport

/**
 * A class encapsulating a single database table
 */
class DbTable (name: String, columns: List[DbColumn], primaryKey: PrimaryKey, foreignKeys: List[ForeignKey], indexes: List[Index]) extends SlickConvertible with FileGenerationSupport{

  private var slickString: String = null

  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString: String = {
    if (slickString == null){
      appendln("object " + underscoredToCamelCase(name) + " extends Table[" + getRecordType + "](\"" + name + "\") {")
      indent()
      //Adding columns
      for (column <- columns){
        appendln(column.toSlickString)
      }
      appendln(getWildcardRow)

      val autoIncrementColumns = columns.filter(_.getAutoIncrement)
      if (autoIncrementColumns.length == 1){
        appendln(getAutoIncrementHelperRow(autoIncrementColumns.head, columns))
      }

//      appendln(getOnlyRequiredHelperRow(columns))

      //Adding primary key
      appendln(primaryKey.toSlickString)

      //Adding foreign keys
      for (foreignKey <- foreignKeys){
        appendln(foreignKey.toSlickString)
      }

      //Adding indexes
      for (index <- indexes){
        appendln(index.toSlickString)
      }
      outdent()
      appendln("}")
      slickString = getClassContents
    }
    slickString
  }

  private def getRecordType: String = {
    "(" + columns.map(
      column => if (column.getNullable)
        "Option[" + column.getJavaType + "]"
      else column.getJavaType
    ).mkString(", ") + ")"
  }

  private def getWildcardRow: String = "def * = " + columns.mkString(" ~ ")

  private def getAutoIncrementHelperRow(autoIncColumn: DbColumn, columns: List[DbColumn]): String =
      "def withAutoInc = " +
      columns.filter(!_.getAutoIncrement).mkString(" ~ ") +
      " returning " + autoIncColumn

}
