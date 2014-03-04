package ru.meridor.diana.db.util

/**
 *
 */
class ForeignKey (name: String, fromTableColumns: List[String], toTable: String, toTableColumns: List[String]) extends SlickConvertible {
  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString: String =
      "def " + underscoredToCamelCase(name, lcFirst = true) +
      " = foreignKey(\"" + name + "\", (" + fromTableColumns.map(tableName => underscoredToCamelCase(tableName, lcFirst = true)).mkString(", ")+ "), " +
      underscoredToCamelCase(toTable) + ")(t => (t." + toTableColumns.map(tableName => underscoredToCamelCase(tableName, lcFirst = true)).mkString(", t.") + "))"
}
