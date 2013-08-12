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

  override def toString = underscoredToCamelCase(name, lcFirst = true)

  def getDatabaseName = name

  def getAutoIncrement = autoIncrement

  def getJavaType = getColumnJavaType(sqlType, size)

  def getNullable = nullable

  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString(): String = "def " + underscoredToCamelCase(name, lcFirst = true) +
    " = column[" + getJavaType + "]" +
    "(" +
      "\"" + getDatabaseName + "\"" +
      (if (nullable) "" else ", O.NotNull") +
      (if (autoIncrement) ", O.AutoInc" else "") +
//TODO: implement default value support
//        (if (defaultValue != null) ", O.Default[" + getJavaType + "](" + getValueEscapeSymbol(getJavaType) + defaultValue + getValueEscapeSymbol(getJavaType) + ")" else "") +
    ")"
}
