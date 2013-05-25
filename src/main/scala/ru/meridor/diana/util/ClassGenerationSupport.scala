package ru.meridor.diana.util

import java.io.File
import org.apache.commons.io.FileUtils

/**
 * Contains a set of methods to generate other classes using a string builder
 */
trait ClassGenerationSupport {
  /**
   * Stores a single indent text
   */
  protected val INDENT: String = "  "

  /**
   * Stores indent string
   */
  private var indentation: String = ""

  /**
   * Stores text of the class currently being generated
   */
  private val classContents: StringBuffer = new StringBuffer

  protected def indent {
    indentation += INDENT
    classContents.append(INDENT)
  }

  protected def outdent {
    indentation = indentation.substring(indentation.length - INDENT.length)
    classContents.delete(classContents.length - INDENT.length, classContents.length)
  }

  protected def append(s: String) {
    classContents.append(s)
  }

  protected def appendln() {
    appendln("")
  }

  protected def appendln(s: String) {
    classContents.append(s + "\n" + indentation)
  }

  /**
   * Generate comments that this class was generated automatically
   */
  protected def appendInfoHeader {
    appendln("/************************************************************")
    appendln(" * DO NOT EDIT THIS CLASS - IT WAS GENERATED AUTOMATICALLY. *")
    appendln(" ************************************************************/")
  }

  /**
   * Gets generated class contents
   * @return currentClass
   */
  protected def getClassContents: String = {
    return classContents.toString
  }

  /**
   * Outputs class contents to the file
   * @param className
   * @throws java.io.IOException
   * @throws java.net.URISyntaxException
   */
  protected def writeToFile(outputDirectory: String, className: String) {
    val dir: File = new File(outputDirectory)
    dir.mkdirs
    FileUtils.writeStringToFile(new File(dir, className + ".scala"), getClassContents)
  }

  /**
   * Converts UNDERSCORED_STRING to camelCaseString
   * @param s
   * @return
   */
  protected def underscoredToCamelCase(s: String): String = {
    val parts = s.split("_")
    var camelCaseString = ""
    for (part <- parts) {
      camelCaseString = camelCaseString + toProperCase(part)
    }
    return camelCaseString
  }

  /**
   * Converts camelCaseString to UNDERSCORED_STRING
   * @param camelCaseString
   * @return
   */
  protected def camelCaseToUnderscored(camelCaseString: String): String = {
    val parts = camelCaseString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
    var ret = ""
    var partNumber: Int = 0
    for (part <- parts) {
      if (partNumber != 0) {
        ret += "_"
      }
      ret += part.toUpperCase
      partNumber += 1
    }
    return ret
  }

  private def toProperCase(s: String): String = {
    return s.substring(0, 1).toUpperCase + s.substring(1).toLowerCase
  }

}