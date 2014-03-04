package ru.meridor.diana.db.util

import java.sql.Types
import ru.meridor.diana.util.CamelCaseSupport

/**
 * A trait to be used to convert JDBC metadata to Slick definition strings
 */
trait SlickConvertible extends CamelCaseSupport {
  /**
   * Returns a slick table definition string
   * @return
   */
  def toSlickString: String

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
      case Types.OTHER => "Timestamp"
      case _ => throw new IllegalArgumentException("Unsupported PostgreSQL type: " + sqlType)
    }
  }

  protected def getValueEscapeSymbol(javaType: String):String = javaType match{
    case "String" | "Blob" | "Date" | "Timestamp" => "\""
    case "Character" => "'"
    case _ => ""
  }

}
