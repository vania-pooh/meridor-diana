package ru.meridor.diana

import ru.meridor.diana.db.{ConnectionPooler, FlywaySupport}
import ru.meridor.diana.log.LoggingSupport

class DianaServlet extends DianaStack
  with FlywaySupport
  with LoggingSupport {
  
  /**
   * Actions to be done when starting application
   */
  override def init(){
    logger.info("Starting application...")
    super.init()
    migrateDatabase()
    initRoutes()
  }

  /**
   * Actions to be done when shutting down application
   */
  override def destroy() {
    super.destroy()
    ConnectionPooler.shutdown()
  }

  /**
   * Initializes application routes
   */
  private def initRoutes(){
    logger.info("Initializing routes...")
    get("/") {
      <html>
        <body>
          <h1>Diana works!</h1>
        </body>
      </html>
    }
  }

  /**
   * Renders a single application view
   * @param viewName
   * @param attributes
   * @return
   */
  private def renderView(viewName: String, attributes: (String, Any)*): String = {
    logger.info("Rendering view \"" + viewName + "\"...")
    contentType = "text/html"
    jade(viewName, attributes:_*)
  }

}
