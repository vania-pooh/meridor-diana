package ru.meridor.diana.db.util

/**
 *
 */
class ForeignKey (name: String, fromTableColumns: List[String], toTable: String, toTableColumns: List[String]) extends SlickConvertible {
  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString(): String = {
    return "def " + underscoredToCamelCase(name, true) +
      " = foreignKey(\"" + name + "\", (" + fromTableColumns.map(tableName => underscoredToCamelCase(tableName, true)).mkString(", ")+ "), " +
      underscoredToCamelCase(toTable) + ")(t => (t." + toTableColumns.map(tableName => underscoredToCamelCase(tableName, true)).mkString(", t.") + "))"
  }
}
