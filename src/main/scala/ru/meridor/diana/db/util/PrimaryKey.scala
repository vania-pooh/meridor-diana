package ru.meridor.diana.db.util

/**
 * Encapsulates Slick primary key
 */
class PrimaryKey(name: String, columns: List[String]) extends SlickConvertible{
  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString(): String = {
    return "def pk = primaryKey(\"" + name + "\", (" + columns.mkString(", ") + "))"
  }
}
