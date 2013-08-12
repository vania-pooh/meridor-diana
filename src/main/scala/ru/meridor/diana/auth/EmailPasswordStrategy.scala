package ru.meridor.diana.auth

import ru.meridor.diana.db.entities.User
import org.scalatra.auth.ScentryStrategy
import org.scalatra.ScalatraBase
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

/**
 *
 */
class EmailPasswordStrategy(protected val app: ScalatraBase) extends ScentryStrategy[User]{
  /**
   * Does actual authentication request
   * @param request
   * @param response
   * @return
   */
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    None
  }
}
