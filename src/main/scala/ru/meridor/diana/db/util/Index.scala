package ru.meridor.diana.db.util

/**
 * Encapsulates a database table index
 */
class Index (name: String, columns: List[String], unique: Boolean) extends SlickConvertible{
  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString: String = {
    "def " + underscoredToCamelCase(name, lcFirst = true) + " = index(\"" + name + "\", (" + columns.map(tableName => underscoredToCamelCase(tableName, lcFirst = true)).mkString(", ") + ")" +
      (if (unique)  ", unique = true" else "") + ")"
  }
}
