package ru.meridor.diana.db.entities

import ru.meridor.diana.util.EqualsById

/**
 * Encapsulates service group
 */
class ServiceGroup(id: Int, displayName: String, sequence: Int) extends EqualsById[ServiceGroup, Int]{

  def getId = id
  def getDisplayName = displayName
  def getSequence = sequence

}
