package io.critica.persistence.db

import org.jetbrains.exposed.sql.Table

object GamePlayers: Table() {
    val game = reference("game", Games)
    val player = reference("player", Players)

    override val primaryKey = PrimaryKey(game, player, name = "pk_game_player")
}
