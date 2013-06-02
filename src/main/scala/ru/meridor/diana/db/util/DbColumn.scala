package ru.meridor.diana.db.util

/**
 * Stores information about a single database column
 */
class DbColumn(
                name: String,
                sqlType: Int,
                sqlTypeName: String,
                size: Int = 0,
                nullable: Boolean,
                autoIncrement: Boolean = false,
                defaultValue: Any = null
) extends SlickConvertible{

  override def toString: String = underscoredToCamelCase(name, true)

  def getDatabaseName: String = name

  def getJavaType: String = {
    return getColumnJavaType(sqlType, size)
  }

  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString(): String = {
    return "def " + underscoredToCamelCase(name, true) +
      " = column[" + getJavaType + "]" +
      "(" +
        "\"" + getDatabaseName + "\"" +
        (if (nullable) "" else ", O.NotNull") +
        (if (autoIncrement) ", O.AutoInc" else "") +
//TODO: implement default value support
//        (if (defaultValue != null) ", O.Default[" + getJavaType + "](" + getValueEscapeSymbol(getJavaType) + defaultValue + getValueEscapeSymbol(getJavaType) + ")" else "") +
      ")"
  }
}
