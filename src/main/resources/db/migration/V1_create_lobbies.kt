package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.transactions.transaction

class V1__create_lobbies: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {

        }
    }
}