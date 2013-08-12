package ru.meridor.diana.db.util

import ru.meridor.diana.db.FlywaySupport

/**
 * A simple application to migrate the database
 */
object MigrateDB extends App with FlywaySupport {
  migrateDatabase()
}
