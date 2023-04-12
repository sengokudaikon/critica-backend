package io.critica.persistence.db

import org.jetbrains.exposed.sql.Table

object GamePlayerTable: Table() {
    val game = reference("game", GameTable)
    val player = reference("player", PlayerTable)

    override val primaryKey = PrimaryKey(game, player, name = "pk_game_player")
}
