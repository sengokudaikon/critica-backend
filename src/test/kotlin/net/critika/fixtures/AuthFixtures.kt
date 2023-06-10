package net.critika.fixtures

import io.github.serpro69.kfaker.Faker
import net.critika.application.user.command.UserCommand
import net.critika.application.user.query.UserQuery

object AuthFixtures {
    private val faker = Faker()

    fun createCommand(): UserCommand.Create {
        return UserCommand.Create(
            email = faker.internet.email(),
            playerName = faker.name.firstName(),
        )
    }

    fun signInCommand(): UserCommand.SignIn {
        return UserCommand.SignIn(
            email = faker.internet.email(),
        )
    }

    fun existsQuery(): UserQuery.Exists {
        return UserQuery.Exists(
            email = faker.internet.email(),
        )
    }
}
