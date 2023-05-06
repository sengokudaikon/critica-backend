package net.critika.story.game

// GameSimulationTest.kt

import io.qameta.allure.Feature
import io.qameta.allure.Story
import kotlinx.coroutines.runBlocking
import net.critika.domain.PlayerRole
import net.critika.story.game.steps.GameSteps
import net.critika.story.game.steps.LobbySteps
import net.critika.story.game.steps.UserSteps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@Feature("Game Simulation")
class GameSimulationStory : KoinComponent {

    private val userSteps: UserSteps by inject()
    private val lobbySteps: LobbySteps by inject()
    private val gameSteps: GameSteps by inject()

    @Story("Simulate game process")
    @Test
    fun `simulate game process`() = runBlocking {
        // 1) user1 registers
        val user1 = userSteps.register("test@mail.ru", "user1", "playerName", "password").getOrNull()
        if (user1 == null) {
            assert(false)
            return@runBlocking
        }

        // 2) user-admin creates a lobby
        val userAdmin = userSteps.login(null, "user-admin", "password").getOrNull()
        if (userAdmin == null) {
            assert(false)
            return@runBlocking
        }
        val lobby = lobbySteps.create(userAdmin.id.value)

        // 3) user1 joins lobby
        lobbySteps.addPlayer(UUID.fromString(lobby.id), user1.username)

        // 4) user-admin creates game
        lobbySteps.addGame(lobby.id, "user-admin")

        val game = lobby.games.firstOrNull() ?: run {
            assert(false)
            return@runBlocking
        }

        // 5) user1 joining said game
        gameSteps.joinGame(user1.id.value, UUID.fromString(game.id))

        // 6) fixtured users (9) are added to the game by user-admin
        val users = listOf(
            "player1", "player2", "player3",
            "player4", "player5", "player6",
            "player7", "player8", "player9",
        )

        users.forEach { username ->
            userSteps.register("$username@mail.ru", username, username, "password")
            lobbySteps.addPlayer(UUID.fromString(lobby.id), username)
            val user = userSteps.login(null, username, "password").getOrNull() ?: return@forEach
            gameSteps.joinGame(user.id.value, UUID.fromString(game.id))
        }

        // 7) user-admin starts the game when there are enough players (10)
        gameSteps.startGame(UUID.fromString(game.id))

        // 8) some time passes, user-admin finishes the game, random 3 players were mafia and have won the game,
        // statistics for them is recorded
        val mafiaCount = 3
        val winningRole = PlayerRole.MAFIA
        val randomMafiaPlayers = game.players.shuffled().take(mafiaCount).map { it.id }

        // Set winning role for random players
        randomMafiaPlayers.forEach { playerId ->
            game.players.find { it.id == playerId }?.apply { role = winningRole }
        }

        // 9) the end result should be the result of GameUseCase.finish which is a map of winningRole to List<PlayerResponse>
        val result = gameSteps.finish(UUID.fromString(game.id), winningRole)
        val winners = result[winningRole]

        assertEquals(mafiaCount, winners?.size)
        winners?.forEach { player ->
            assertTrue(randomMafiaPlayers.contains(player.id.value))
            assertEquals(winningRole.toString(), player.role)
        }
    }
}
