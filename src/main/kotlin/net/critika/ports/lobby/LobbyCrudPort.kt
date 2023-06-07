package net.critika.ports.lobby

import net.critika.application.lobby.command.LobbyCommand
import net.critika.application.lobby.response.LobbyResponse
import net.critika.ports.CrudPort

interface LobbyCrudPort : CrudPort<LobbyCommand, LobbyResponse>
