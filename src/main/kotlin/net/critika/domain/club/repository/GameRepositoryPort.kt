package net.critika.domain.club.repository

import net.critika.application.game.command.GameCommand
import net.critika.domain.club.model.Game
import net.critika.ports.CrudPort

interface GameRepositoryPort : CrudPort<GameCommand, Game>
