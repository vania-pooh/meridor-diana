package ru.meridor.diana.db.entities

/**
 * Encapsulates database user
 */
class User(email: String){

}

object User{
  def login():Option[User] = {
    None
  }
}
