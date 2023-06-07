package net.critika.unit

import kotlinx.uuid.UUID
import net.critika.domain.club.model.Game
import net.critika.domain.club.model.GameStatus
import net.critika.domain.club.model.Lobby
import net.critika.domain.gameprocess.model.Player
import net.critika.domain.user.model.User
import net.critika.domain.user.model.UserRating
import net.critika.domain.user.model.UserRole
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.emptySized
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

object Helpers {

    fun getMockUser(): User {
        val user = mock<User> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { username } doReturn "username"
            on { password } doReturn "encoded_password"
            on { email } doReturn "email@example.com"
            on { role } doReturn UserRole.USER
        }
        return user
    }

    fun getMockUserRating(): UserRating {
        val userRating = mock<UserRating> {
            on { totalPoints } doReturn 0
            on { bonusPoints } doReturn 0
            on { malusPoints } doReturn 0
            on { bestMovePoints } doReturn 0
        }

        return userRating
    }

    fun getMockLobby(): Lobby {
        val date = mock<LocalDateTime>().toString()
        return mock<Lobby> {
            on { date } doReturn date
            on { id } doReturn mock<EntityID<UUID>>()
            on { games } doReturn emptySized<Game>()
            on { players } doReturn emptySized<Player>()
        }
    }

    fun getMockGame(): Game {
        return mock<Game> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { date } doReturn LocalDateTime.now()
            on { status } doReturn GameStatus.WAITING
            on { players } doReturn emptySized()
            on { dayEvents } doReturn emptySized()
            on { nightEvents } doReturn emptySized()
        }
    }

    fun getMockPlayer(): Player {
        return mock<Player> {
            on { id } doReturn mock<EntityID<UUID>>()
            on { name } doReturn "TestPlayer"
        }
    }

    fun getMockLobbyWithPlayer(): Lobby {
        val lobby = getMockLobby()
        val player = getMockPlayer()
        whenever(lobby.players) doReturn emptySized<Player>().apply { plus(player) }
        return lobby
    }
}
