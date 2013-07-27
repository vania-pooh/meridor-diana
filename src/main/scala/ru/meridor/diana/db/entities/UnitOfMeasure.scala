package ru.meridor.diana.db.entities

import ru.meridor.diana.util.EqualsById

/**
 * Encapsulates a single unit of measure, like meters, barrels, inches, etc.
 */
class UnitOfMeasure(id: Int, displayName: String) extends EqualsById[UnitOfMeasure, Int]{

  def getId = id
  def getDisplayName = displayName

}
