package ru.meridor.diana.db.util

import ru.meridor.diana.util.FileGenerationSupport

/**
 * A class encapsulating a single database table
 */
class DbTable (name: String, columns: List[DbColumn], primaryKey: PrimaryKey, foreignKeys: List[ForeignKey], indexes: List[Index]) extends SlickConvertible with FileGenerationSupport{

  private var slickString: String = null;

  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString(): String = {
    if (slickString == null){
      appendln("object " + underscoredToCamelCase(name) + " extends Table[" + getRecordType() + "](\"" + name + "\") {")
      indent
      //Adding columns
      for (column <- columns){
        appendln(column.toSlickString())
      }
      appendln(getWildcardRow())

      //Adding primary key

      //Adding foreign keys
      for (foreignKey <- foreignKeys){
        appendln(foreignKey.toSlickString())
      }

      //Adding indexes
      for (index <- indexes){
        appendln(index.toSlickString())
      }
      outdent
      appendln("}")
      slickString = getClassContents
    }
    return slickString
  }

  private def getRecordType(): String = {
    return "(" + columns.map(column => column.getJavaType).mkString(", ") + ")"
  }

  private def getWildcardRow(): String = {
    return "def * = " + columns.mkString(" ~ ")
  }
}
