package ru.meridor.diana.util

/**
 * Implement a behavior when two instances are equal when their IDs returned by getId() method are equals
 */
trait EqualsById[U <: EqualsById[U, V], V] {
  def getId: V

  override def equals(anotherUnit: Any): Boolean =
    anotherUnit.isInstanceOf[EqualsById[_, V]] && getId.equals(anotherUnit.asInstanceOf[EqualsById[_, V]].getId)

}
