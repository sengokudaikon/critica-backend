package net.critika.persistence.club.repository

import kotlinx.uuid.UUID
import net.critika.application.club.command.ClubCommand
import net.critika.domain.club.model.Club
import net.critika.domain.club.model.RuleSet
import net.critika.domain.club.repository.ClubRepositoryPort
import net.critika.domain.user.model.User
import net.critika.infrastructure.exception.ClubException
import net.critika.infrastructure.exception.UserException
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.time.LocalDateTime

class ClubRepository : ClubRepositoryPort {
    override suspend fun create(command: ClubCommand): Club = suspendedTransactionAsync {
        command as ClubCommand.Create
        Club.new {
            name = command.name
            creator = User.findById(command.creatorId) ?: throw UserException.NotFound("User not found")
            country = command.country
            logo = command.logo
            description = command.description
            ruleSet = RuleSet.valueOf(command.ruleSet)
        }
    }.await()

    override suspend fun get(id: UUID): Club = suspendedTransactionAsync {
        Club.findById(id) ?: throw ClubException.NotFound("Club not found")
    }.await()

    override suspend fun update(command: ClubCommand): Club = suspendedTransactionAsync {
        command as ClubCommand.Update
        val club = Club[command.id]
        club.name = command.name ?: club.name
        club.country = command.country ?: club.country
        club.logo = command.logo ?: club.logo
        club.description = command.description ?: club.description
        club.ruleSet = command.ruleSet?.let { RuleSet.valueOf(it) } ?: club.ruleSet
        club.updatedAt = LocalDateTime.now()
        club
    }.await()

    override suspend fun delete(id: UUID): Unit = suspendedTransactionAsync {
        Club.findById(id)?.delete() ?: throw ClubException.NotFound("Club not found")
    }.await()

    override suspend fun addUserToClub(userId: UUID, clubId: UUID): Boolean = suspendedTransactionAsync {
        val club = Club.findById(clubId) ?: return@suspendedTransactionAsync false
        club.members.plus(User[userId])
        true
    }.await()

    override suspend fun removeUserFromClub(userId: UUID, clubId: UUID): Boolean = suspendedTransactionAsync {
        val club = Club.findById(clubId) ?: return@suspendedTransactionAsync false
        club.members.minus(User[userId])
        true
    }.await()

    override suspend fun list(): List<Club> = suspendedTransactionAsync {
        Club.all().toList()
    }.await()
}
