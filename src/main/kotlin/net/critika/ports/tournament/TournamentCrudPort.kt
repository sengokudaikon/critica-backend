package net.critika.ports.tournament

import net.critika.application.tournament.command.TournamentCommand
import net.critika.application.tournament.response.TournamentResponse
import net.critika.ports.CrudPort

interface TournamentCrudPort : CrudPort<TournamentCommand, TournamentResponse>
