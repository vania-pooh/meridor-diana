package ru.meridor.diana

import org.slf4j.LoggerFactory
import ru.meridor.diana.db.SlickSupport
import ru.meridor.diana.db.FlywaySupport

class DianaServlet extends DianaStack with SlickSupport with FlywaySupport {
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  logger.info("Starting application...")
  
  logger.info("Initializing routes...")
  get("/") {
    <html>
      <body>
        <h1>Diana works!</h1>
      </body>
    </html>
  }
  
  private def renderView(viewName: String, attributes: (String, Any)*): String = {
    logger.info("Rendering view \"" + viewName + "\"...")
    contentType = "text/html"
    jade(viewName, attributes:_*)
  }
  
}
